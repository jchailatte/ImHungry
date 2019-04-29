import React from "react";
import NetworkRequest from "../util/NetworkRequest";
import { Stack, Text, PrimaryButton } from "office-ui-fabric-react";
import ImageCollage from "../components/ImageCollage";
import { mergeStyleSets } from "office-ui-fabric-react/lib/Styling";
import RecipeResults from "../components/RecipeResults";
import { withRouter } from "react-router-dom";
import RestaurantResults from "../components/RestaurantResults";
import LoginCommandBar from "../components/LoginCommandBar";

@withRouter
export default class ResultsPage extends React.Component {
    constructor(props) {
        super(props);

        const urlParameters = new URLSearchParams(window.location.search);
        const query = urlParameters.get("query");
        const numberOfResults = urlParameters.get("numberOfResults");
        const radius = urlParameters.get("radius");

        this.state = {
            // Input
            query,
            numberOfResults,
            radius,

            //button states
            hide1:false,
            hide2:false,
            hide3:false,
            hide4:false,
            //restaurant button states
            hide5:false,
            hide6:false,
            hide7:false,
            hide8:false,

            // Data
            images: undefined,
            recipes: undefined,
            restaurants: undefined,
            currrecipes: undefined,
            currrest: undefined,
            recpage: 1,
            respage: 1,

            searchHistory: undefined
        };

        // Save data to sessionStorage so "back to results" works
        window.sessionStorage.setItem("query", query);
        window.sessionStorage.setItem("numberOfResults", numberOfResults);
        window.sessionStorage.setItem("radius", radius);

        document.title = "Results";
    }

    /**
     * Query parameters to send to backend
     *
     * @returns {{query: string, numberOfResults: number, radius: number}}
     */
    get queryParameters() {
        return {
            query: this.state.query,
            numberOfResults: this.state.numberOfResults,
            radius: this.state.radius
        };
    }

    /**
     * Runs each data fetching Promise concurrently
     */
    componentDidMount() {
        // this.getImages();
        this.getRecipes();
        this.getRestaurants();
        this.saveToHistory();
        this.checkPages();
        this.getSearchHistory();
    }

    /**
     * Makes request to backend for 10 images for this query and saves to state.
     *
     * @returns {Promise<void>} on completion
     */
    getImages = async () => {
        try {
            const response = await NetworkRequest.get("/api/image-collage/", {
                query: this.state.query
            });
            if (response.ok) {
                this.setState({
                    images: await response.json()
                });
            } else {
                console.error(response.status, "Could not get images");
            }
        } catch (exception) {
            console.error("Fatal error when trying to get recipes", exception);
        }
    };

    /**
     * Makes request to backend for recipes for this query and saves to state.
     *
     * @returns {Promise<void>} on completion
     */
    getRecipes = async () => {
        try {
            const response = await NetworkRequest.get(
                "/api/recipe/",
                this.queryParameters
            );
            if (response.ok) {
                const data = await response.json();

                const temparray = [];
                const j = data.length;
                const chunk = 5;
                let inc = 0;
                for (let i = 0; i < j; i += chunk) {
                    temparray[inc] = data.slice(i, i + chunk);
                    inc++;
                }
                this.setState({
                    recipes: temparray,
                    currrecipes: temparray.length > 0 ? temparray[0] : []
                });
            } else {
                console.error(response.status, "Could not get recipes");
            }
        } catch (exception) {
            console.error("Fatal error when trying to get recipes", exception);
        }
    };

    /**
     * Makes request to backend for restaurants for this query and saves to state.
     *
     * @returns {Promise<void>} on completion
     */

    getRestaurants = async () => {
        try {
            const response = await NetworkRequest.get(
                "/api/restaurant/",
                this.queryParameters
            );
            if (response.ok) {
                const data = await response.json();

                const temparray = [];
                const j = data.length;
                const chunk = 5;
                let inc = 0;
                for (let i = 0; i < j; i += chunk) {
                    temparray[inc] = data.slice(i, i + chunk);
                    inc++;
                }
                this.setState({
                    restaurants: temparray,
                    currrest: temparray.length > 0 ? temparray[0] : []
                });
            } else {
                console.error(response.status, "Could not get restaurants");
            }
        } catch (exception) {
            console.error("Fatal error when trying to get restaurants", exception);
        }
    };

    /**
     * Gets search history for the user
     *
     * @returns {Promise<void>}
     */
    getSearchHistory = async () => {
        const getImages = async (query) => {
            return [];
            // try {
            //     const response = await NetworkRequest.get("/api/image-collage/", {
            //         query
            //     });
            //     if (response.ok) {
            //         return await response.json();
            //     } else {
            //         console.error(response.status, "Could not get images");
            //     }
            // } catch (exception) {
            //     console.error("Fatal error when trying to get recipes", exception);
            // }
        };

        try {
            const response = await NetworkRequest.get("/api/search-history/");
            if (response.ok) {
                const data = await response.json();
                Promise.all(data.map(item => getImages(item)))
                    .then(values => {
                        this.setState({
                            bottomImages: values,
                            searchHistory: data
                        });
                    });
            }
        }
        catch (exception) {
            console.error("Fatal error when getting search history", exception);
        }
    };

    /**
     * Wraps react-router-dom for redirection
     *
     * @param url
     */
    redirect = url => {
        this.props.history.push(url);
    };

    /**
     * Notifies backend of search term to save to user history
     *
     * @returns {Promise<void>} on completion
     */
    saveToHistory = async () => {
        await NetworkRequest.post(
            "/api/search-history/",
            {query: this.state.query}
        );
    };

    checkPages()
    {
        if(Math.ceil(this.state.numberOfResults/5) >= 2)
        {
            this.setState({
                hide3:true,
                hide7:true
            });
        }
        if(Math.ceil(this.state.numberOfResults/5) >= 3)
        {
            this.setState({
                hide4:true,
                hide8:true
            })
        }
    }

    pageUp = () => {
        this.setState(prevState => {
            return {
                recpage:
                    prevState.recpage + 1 <= (Math.ceil(prevState.numberOfResults / 5))
                        ? prevState.recpage + 1
                        : prevState.recpage
            };
        }, () => {
            this.setState(prevState => {
                return {
                    currrecipes: prevState.recipes[prevState.recpage - 1],
                }
            });

            if(this.state.recpage > 2)
            {
                this.setState({hide1:true});
            }

            if(this.state.recpage > 1)
            {
                this.setState({hide2:true});
            }

            if(this.state.recpage > (Math.ceil(this.state.numberOfResults/5)) - 1)
            {
                this.setState({hide3:false});
            }
            if(this.state.recpage > (Math.ceil(this.state.numberOfResults / 5)) - 2)
            {
                this.setState({hide4:false});
            }

        });
    };

    pageDown = () => {
        this.setState(prevState => {
            return {
                recpage: prevState.recpage - 1 > 0 ? prevState.recpage - 1 : prevState.recpage
            };
        }, () => {
            this.setState(prevState => {
                return {
                    currrecipes: prevState.recipes[prevState.recpage - 1],
                }
            });

            if(this.state.recpage <= 2)
            {
                this.setState({hide1:false});
            }

            if(this.state.recpage <= 1)
            {
                this.setState({hide2:false});
            }

            if(this.state.recpage <= (Math.ceil(this.state.numberOfResults / 5)) - 1)
            {
                this.setState({hide3:true});
            }

            if(this.state.recpage <= (Math.ceil(this.state.numberOfResults / 5)) - 2)
            {
                this.setState({hide4:true});
            }
        });
    };

    recChangePage =(event,val)=> {
        this.setState({
                recpage: val,
                currrecipes: this.state.recipes[val-1]
        }, () => {

            if (this.state.recpage <= 2) {
                this.setState({hide1: false});
            }

            if (this.state.recpage <= 1) {
                this.setState({hide2: false});
            }

            if (this.state.recpage <= (Math.ceil(this.state.numberOfResults / 5)) - 1) {
                this.setState({hide3: true});
            }

            if (this.state.recpage <= (Math.ceil(this.state.numberOfResults / 5)) - 2) {
                this.setState({hide4: true});
            }

            if (this.state.recpage > 2) {
                this.setState({hide1: true});
            }

            if (this.state.recpage > 1) {
                this.setState({hide2: true});
            }

            if (this.state.recpage > (Math.ceil(this.state.numberOfResults / 5)) - 1) {
                this.setState({hide3: false});
            }
            if (this.state.recpage > (Math.ceil(this.state.numberOfResults / 5)) - 2) {
                this.setState({hide4: false});
            }

        });
    };

    pageUp2 = () => {
        this.setState(prevState => {
            return {
                respage:
                    prevState.respage + 1 <= (Math.ceil(prevState.numberOfResults / 5))
                        ? prevState.respage + 1
                        : prevState.respage
            };
        }, () => {
            this.setState(prevState => {
                return {
                    currrest: prevState.restaurants[prevState.respage - 1]
                }
            });

            if(this.state.respage > 2)
            {
                this.setState({hide5:true});
            }

            if(this.state.respage > 1)
            {
                this.setState({hide6:true});
            }

            if(this.state.respage > (Math.ceil(this.state.numberOfResults/5)) - 1)
            {
                this.setState({hide7:false});
            }
            if(this.state.respage > (Math.ceil(this.state.numberOfResults / 5)) - 2)
            {
                this.setState({hide8:false});
            }

        });
    };

    pageDown2 = () => {
        this.setState(prevState => {
            return {
                respage: prevState.respage - 1 > 0 ? prevState.respage - 1 : prevState.respage
            };
        }, () => {
            this.setState(prevState => {
                return {
                    currrest: prevState.restaurants[prevState.respage - 1]
                }
            });

            if(this.state.respage <= 2)
            {
                this.setState({hide5:false});
            }

            if(this.state.respage <= 1)
            {
                this.setState({hide6:false});
            }

            if(this.state.respage <= (Math.ceil(this.state.numberOfResults / 5)) - 1)
            {
                this.setState({hide7:true});
            }

            if(this.state.respage <= (Math.ceil(this.state.numberOfResults / 5)) - 2)
            {
                this.setState({hide8:true});
            }
        });
    };

    resChangePage =(event,val)=> {
        this.setState({
            respage: val,
            currrest: this.state.restaurants[val-1]
        }, () => {

            if (this.state.respage <= 2) {
                this.setState({hide5: false});
            }

            if (this.state.respage <= 1) {
                this.setState({hide6: false});
            }

            if (this.state.respage <= (Math.ceil(this.state.numberOfResults / 5)) - 1) {
                this.setState({hide7: true});
            }

            if (this.state.respage <= (Math.ceil(this.state.numberOfResults / 5)) - 2) {
                this.setState({hide8: true});
            }

            if (this.state.respage > 2) {
                this.setState({hide5: true});
            }

            if (this.state.respage > 1) {
                this.setState({hide6: true});
            }

            if (this.state.respage > (Math.ceil(this.state.numberOfResults / 5)) - 1) {
                this.setState({hide7: false});
            }
            if (this.state.respage > (Math.ceil(this.state.numberOfResults / 5)) - 2) {
                this.setState({hide8: false});
            }

        });
    };

    render() {
        const styles = mergeStyleSets({
            collage: {
                width: "100vw",
                overflow: "hidden"
            },
            textCenter: {
                textAlign: "center"
            }
        });

        return (
            <>
                <LoginCommandBar
                    redirect={this.redirect}
                    showResultsButton/>
                <Stack >
                    <Stack.Item>
                        <Stack
                            horizontal
                            horizontalAlign="space-around"
                            verticalAlign="center"
                        >
                            <Stack.Item className={styles.collage}>
                                <div
                                    style={{
                                        position: "relative",
                                        height: "40vh",
                                        marginBottom: "1rem"
                                    }}
                                >
                                    <ImageCollage images={this.state.images} />
                                    <div
                                        style={{
                                            alignItems: "center",
                                            display: "flex",
                                            position: "absolute",
                                            height: "100%",
                                            width: "100%"
                                        }}
                                    >
                                        <div
                                            style={{
                                                background: "rgba(255, 255, 255, .9)",
                                                width: "100%"
                                            }}
                                        >
                                            <div className="ms-Grid-col ms-sm12 ms-hiddenMdDown" style={{color:"#0078d4"}}>
                                                <Text
                                                    as="h1"
                                                    block
                                                    className={styles.textCenter}
                                                    variant="mega"
                                                >
                                                    Results for {this.state.query}
                                                </Text>
                                            </div>
                                            <div className="ms-Grid-col ms-sm12 ms-hiddenLgUp">
                                                <Text as="h1" block variant="xxLarge">
                                                    Results for {this.state.query}
                                                </Text>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </Stack.Item>
                        </Stack>
                    </Stack.Item>
                    <Stack.Item>
                        <div className="container ms-Grid-row">
                            <div className="ms-Grid-col ms-sm12 ms-lg6">
                                <div className="ms-Grid-row">
                                    <div className="ms-Grid-col ms-sm12 ms-hiddenMdDown">
                                        <Text
                                            as="h2"
                                            block
                                            className={styles.textCenter}
                                            variant="xxLarge"
                                        >
                                            Restaurants
                                        </Text>
                                    </div>
                                    <div className="ms-Grid-col ms-sm12 ms-hiddenLgUp">
                                        <Text
                                            as="h1"
                                            block
                                            className={styles.textCenter}
                                            variant="xLarge"
                                        >
                                            Restaurants
                                        </Text>
                                    </div>
                                    <div className="ms-Grid-col ms-sm12">
                                        <RestaurantResults
                                            restaurants={this.state.currrest}
                                            resultsPage
                                            redirect={this.redirect}
                                        />
                                        <Stack
                                            horizontal
                                        >
                                            <PrimaryButton
                                                iconProps={{ iconName: "DoubleChevronLeft12" }}
                                                onClick={this.pageDown2}
                                                id="resbackbutton"
                                                name="resbackbutton"
                                                text="Previous"
                                            />
                                            <PrimaryButton
                                                style={{visibility: this.state.hide5 ? 'visible' : 'hidden'}}
                                                onClick = {()=>this.resChangePage(this,this.state.respage-2)}
                                            >
                                                {this.state.respage -2}
                                            </PrimaryButton>
                                            <PrimaryButton
                                                style={{visibility:this.state.hide6 ? 'visible' : 'hidden'}}
                                                onClick = {()=>this.resChangePage(this,this.state.respage-1)}
                                            >
                                                {this.state.respage -1}
                                            </PrimaryButton>
                                            <PrimaryButton
                                                disabled="true"
                                            >
                                                {this.state.respage}
                                            </PrimaryButton>
                                            <PrimaryButton
                                                style={{visibility:this.state.hide7 ? 'visible' : 'hidden'}}
                                                onClick = {()=>this.resChangePage(this,this.state.respage+1)}
                                            >
                                                {this.state.respage +1}
                                            </PrimaryButton>
                                            <PrimaryButton
                                                style={{visibility:this.state.hide8 ? 'visible' : 'hidden'}}
                                                onClick = {()=>this.resChangePage(this,this.state.respage+2)}
                                            >
                                                {this.state.respage +2}
                                            </PrimaryButton>

                                            <PrimaryButton
                                                className={styles.button}
                                                menuIconProps={{ iconName: "DoubleChevronRight12" }}
                                                onClick={this.pageUp2}
                                                id="resnextbutton"
                                                name="resnextbutton"
                                                text="Next"
                                            />
                                        </Stack>
                                    </div>
                                </div>
                            </div>
                            <div className="ms-Grid-col ms-sm12 ms-lg6">
                                <div className="ms-Grid-row">
                                    <div className="ms-Grid-col ms-sm12 ms-hiddenMdDown">
                                        <Text
                                            as="h2"
                                            block
                                            className={styles.textCenter}
                                            variant="xxLarge"
                                        >
                                            Recipes
                                        </Text>
                                    </div>
                                    <div className="ms-Grid-col ms-sm12 ms-hiddenLgUp">
                                        <Text
                                            as="h1"
                                            block
                                            className={styles.textCenter}
                                            variant="xLarge"
                                        >
                                            Recipes
                                        </Text>
                                    </div>
                                    <div className="ms-Grid-col ms-sm12">
                                        <RecipeResults
                                            recipes={this.state.currrecipes}
                                            redirect={this.redirect}
                                        />
                                        <Stack
                                            horizontal
                                        >
                                            <PrimaryButton
                                                iconProps={{ iconName: "DoubleChevronLeft12" }}
                                                onClick={this.pageDown}
                                                id="backbutton"
                                                name="backbutton"
                                                text="Previous"
                                            />
                                            <PrimaryButton
                                                style={{visibility: this.state.hide1 ? 'visible' : 'hidden'}}
                                                onClick = {()=>this.recChangePage(this,this.state.recpage-2)}
                                            >
                                                {this.state.recpage -2}
                                            </PrimaryButton>
                                            <PrimaryButton
                                                style={{visibility:this.state.hide2 ? 'visible' : 'hidden'}}
                                                onClick = {()=>this.recChangePage(this,this.state.recpage-1)}
                                            >
                                                {this.state.recpage -1}
                                            </PrimaryButton>
                                            <PrimaryButton
                                                disabled="true"
                                            >
                                                {this.state.recpage}
                                            </PrimaryButton>
                                            <PrimaryButton
                                                style={{visibility:this.state.hide3 ? 'visible' : 'hidden'}}
                                                onClick = {()=>this.recChangePage(this,this.state.recpage+1)}
                                            >
                                                {this.state.recpage +1}
                                            </PrimaryButton>
                                            <PrimaryButton
                                                style={{visibility:this.state.hide4 ? 'visible' : 'hidden'}}
                                                onClick = {()=>this.recChangePage(this,this.state.recpage+2)}
                                            >
                                                {this.state.recpage +2}
                                            </PrimaryButton>

                                            <PrimaryButton
                                                className={styles.button}
                                                menuIconProps={{ iconName: "DoubleChevronRight12" }}
                                                onClick={this.pageUp}
                                                id="nextbutton"
                                                name="nextbutton"
                                                text="Next"
                                            />
                                        </Stack>
                                    </div>
                                </div>
                            </div>
                            <div />
                        </div>
                    </Stack.Item>
                </Stack>

                <div style={{
                    width: "100vw",
                    overflowX: "scroll"
                }}>
                    <div style={{
                        display: "flex",
                        justifyContent: "center",
                        width: `${this.state.searchHistory ? this.state.searchHistory.length* 400 : 0}px`
                    }}>
                        {
                            this.state.searchHistory && this.state.searchHistory.map((item, index) => {
                                return (
                                    <a
                                        key={index}
                                        style={{
                                            height: "200px",
                                            width: "400px",
                                            position: "relative"
                                        }}
                                        href={`/results/?query=${item}&numberOfResults=5&radius=40233.5`}>
                                        <h1 style={{
                                            color: "black",
                                            position: "absolute",
                                            top: "100px",
                                            transform: "translateY(-50%)",
                                            width: "100%",
                                            textAlign: "center",
                                            zIndex: 10
                                        }}>
                                            {item}
                                        </h1>
                                        <ImageCollage images={this.state.bottomImages[index]}/>
                                    </a>
                                )
                            })
                        }
                    </div>
                </div>
            </>
        );
    }
}
