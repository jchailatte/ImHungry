import React from "react";

import {mergeStyleSets} from "office-ui-fabric-react/lib/Styling";
import {DefaultButton, Dialog, DialogFooter, Stack, Text} from "office-ui-fabric-react";
import {withRouter} from "react-router-dom";
import NetworkRequest from "../util/NetworkRequest";
import LoginCommandBar from "../components/LoginCommandBar";

@withRouter
export default class RecipePage extends React.Component {
    constructor(props) {
        super(props);

        const urlParameters = new URLSearchParams(window.location.search);
        const recipeURL = urlParameters.get("recipeURL");

        this.state = {
            data: undefined,
            recipeURL,
            showAuthorizationDialog: false
        };

        document.title = "Recipe";
    }

    async componentDidMount() {
        try {
            const response = await NetworkRequest.get(
                "/api/recipe/",
                {recipeURL: this.state.recipeURL}
            );
            if (response.ok) {
                const data = await response.json();
                this.setState({data});
            } else {
                console.error(response.status, "Could not get recipe");
            }
        } catch (exception) {
            console.error("Fatal error when trying to get recipe", exception);
        }
    }

    addToGroceryList = async () => {
        try {
            const response = await NetworkRequest.post(
                "/api/grocery-list/",
                {
                    recipeID: this.state.data.recipeID
                }
            );

            if (!response.ok) {
                this.setState({
                    showAuthorizationDialog: true
                });
            }
        }
        catch (exception) {
            console.error("Fatal error when trying to add to grocery list");
        }
    };

    addToList = async (listType) => {
        try {
            const response = await NetworkRequest.post(
                "/api/recipe/",
                {
                    recipeURL: this.state.recipeURL,
                    listType
                }
            );

            if (!response.ok) {
                this.setState({
                    showAuthorizationDialog: true
                });
            }
        }
        catch (exception) {
            console.error("Fatal error when trying to add to list", exception);
        }
    };

    _dismissDialog = () => {
        this.setState(
            {showAuthorizationDialog: false}
        );
    };

    render() {
        const styles = mergeStyleSets({
            floatingImage: {
                height: "20rem",
                margin: "-10rem auto 2rem",
                boxShadow: "0 3px 6px rgba(0,0,0,0.16), 0 3px 6px rgba(0,0,0,0.23)",
                borderRadius: "10px"
            },
            headerContainer: {
                background: "url('/static/img/recipe.jpg')",
                boxShadow: "0 3px 6px rgba(0,0,0,0.16), 0 3px 6px rgba(0,0,0,0.23)",
                color: "white",
                padding: "2rem 0 12rem",
                textAlign: "center"
            },
            ingredientList: {
                margin: "1rem 0",
                padding: "0 1rem"
            },
            textCenter: {
                textAlign: "center",
            }
        });

        if (this.state.data === undefined) {
            return <Text>Loading...</Text>;
        }

        return (
            <>
                <LoginCommandBar
                    addToGroceryList={this.addToGroceryList}
                    addToList={this.addToList}
                    redirect={this.props.history.push}/>
                <Dialog
                    hidden={!this.state.showAuthorizationDialog}
                    onDismiss={this._dismissDialog}
                    dialogContentProps={{
                        title: "Login to continue",
                        subText: "You must be logged in to save items to a list. Registration is free, simple, and easy!"
                    }}>
                    <DialogFooter>
                        <DefaultButton
                            onClick={this._dismissDialog}
                            text="Dismiss"/>
                    </DialogFooter>
                </Dialog>
                <Stack
                    className={`${styles.headerContainer} background-image`}
                    gap={40}>
                    <Stack.Item>
                        <Text variant="mega">
                            {this.state.data.name}
                        </Text>
                    </Stack.Item>
                    <Stack.Item>
                        <Stack
                            gap={40}
                            horizontal
                            horizontalAlign="center">
                            <Stack.Item>
                                <Text block variant="xxLarge">
                                    Prep Time
                                </Text>
                                <Text block variant="large">
                                    {this.state.data.prep_time}
                                </Text>
                            </Stack.Item>
                            <Stack.Item>
                                <Text block variant="xxLarge">
                                    Cook Time
                                </Text>
                                <Text block variant="large">
                                    {this.state.data.cook_time}
                                </Text>
                            </Stack.Item>
                        </Stack>
                    </Stack.Item>
                </Stack>
                <div className="container">
                    <div className={styles.textCenter}>
                        <img
                            alt={`Picture of ${this.state.data.name}`}
                            className={styles.floatingImage}
                            src={this.state.data.image}/>
                    </div>
                    <div
                        className="ms-Grid" dir="ltr">
                        <div className="ms-Grid-row">
                            <div className="ms-Grid-col ms-sm12 ms-md4">
                                <Text variant="xxLarge" >Ingredients</Text>
                                <Text variant="large">
                                    <ul
                                        className={styles.ingredientList}>
                                        {
                                            this.state.data.ingredients.map((item, index) => <li key={index}>{item}</li>)
                                        }
                                    </ul>
                                </Text>
                            </div>
                            <div className="ms-Grid-col ms-sm12 ms-md8">
                                <Text variant="xxLarge" >Instructions</Text>
                                <Text variant="large" >
                                    <ol>
                                        {
                                            this.state.data.steps.map((item, index) => <li key={index}>{item}</li>)
                                        }
                                    </ol>
                                </Text>
                            </div>
                        </div>
                    </div>
                </div>
            </>
        );
    }
}