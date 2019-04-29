import React from "react";
import PropTypes from "prop-types";
import NetworkRequest from "../util/NetworkRequest";
import {Checkbox, IconButton, Stack, Text} from "office-ui-fabric-react";
import {withRouter} from "react-router-dom";
import LoginCommandBar from "../components/LoginCommandBar";
import ListType from "../util/ListType";
import {DragDropContext, Draggable, Droppable} from "react-beautiful-dnd";
import RecipeResult from "../components/RecipeResult";
import RestaurantResult from "../components/RestaurantResult";

@withRouter
export default class ListManagementPage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            groceryItems: undefined,
            listItems: undefined,
            unauthorized: undefined
        };

        document.title = "List Management";
    }

    componentDidMount() {
        this.getData();
    }

    componentDidUpdate(prevProps) {
        if (this.props.match.params.id !== prevProps.match.params.id) {
            this.getData();
        }
    }

    /**
     * Wait for both promises to finish before rendering them since a future feature
     * will require order to be persisted so all results need to be retrieved before display
     *
     * @returns {Promise<any>} when complete with everything
     */
    getData = () => {
        if (parseInt(this.props.match.params.id) === ListType.GROCERY_LIST) {
            return new Promise(resolve => {
                NetworkRequest.get("/api/grocery-list/")
                    .then(response => response.json())
                    .then(data => {
                        this.setState({
                            groceryItems: data
                        });
                    })
                    .then(() => resolve())
            });
        }
        else {
            return new Promise(resolve => {
                Promise.all([
                    this.getRecipes(),
                    this.getRestaurants()
                ])
                    .then(values => {
                        const listItems = [
                            ...values[0],
                            ...values[1]
                        ];
                        listItems.sort((a, b) => a.listOrder - b.listOrder);
                        this.setState({listItems});
                    })
                    .then(() => resolve())
            });
        }
    };

    /**
     * Gets all saved recipes for this provided list type and returns,
     * to be processed in componentDidMount()
     *
     * @returns {Promise<Array>}
     */
    getRecipes = async () => {
        try {
            const response = await NetworkRequest.get(
                "/api/list-management/",
                {
                    query: "recipe",
                    listType: this.props.match.params.id
                }
            );
            if (response.ok) {
                this.setState({
                    unauthorized: false
                });
                return await response.json();
            } else {
                this.setState({
                    unauthorized: true
                });
            }
        } catch (exception) {
            console.error("Fatal error when trying to get recipes", exception);
        }
        return [];
    };

    /**
     * Gets all saved restaurants for this provided list type and returns,
     * to be processed in componentDidMount()
     *
     * @returns {Promise<Array>}
     */
    getRestaurants = async () => {
        try {
            const response = await NetworkRequest.get(
                "/api/list-management/",
                {
                    query: "restaurant",
                    listType: this.props.match.params.id
                }
            );
            if (response.ok) {
                this.setState({
                    unauthorized: false
                });
                return await response.json();
            } else {
                this.setState({
                    unauthorized: true
                });
            }
        } catch (exception) {
            console.error("Fatal error when trying to get recipes", exception);
        }
        return [];
    };

    /**
     * Wraps react-router-dom for redirection
     *
     * @param url
     */
    redirect = (url) => {
        this.props.history.push(url);
    };

    onDragEnd = result => {
        if (result.source && result.destination) {
            this.setState(prevState => {
                const listItems = [...prevState.listItems];
                const [removed] = listItems.splice(result.source.index, 1);
                listItems.splice(result.destination.index, 0, removed);
                return {
                    listItems
                };
            }, () => {
                let listItems = [...this.state.listItems];
                const reorderedItems = listItems.map((item, index) => {
                    item.listOrder = index;
                    return item;
                });
                const recipes = reorderedItems.filter(item => item.hasOwnProperty("recipeID"));
                const restaurants = reorderedItems.filter(item => item.hasOwnProperty("restaurantID"));
                const recipesToSubmit = recipes.map(item => {
                    return {
                        index: item.listOrder,
                        recipeID: item.recipeID
                    }
                });
                const restaurantsToSubmit = restaurants.map(item => {
                    return {
                        index: item.listOrder,
                        restaurantID: item.restaurantID
                    };
                });
                NetworkRequest.put(
                    "/api/list-management/",
                    {
                        body: recipesToSubmit
                    },
                    {
                        query: "recipe",
                        listType: this.props.match.params.id
                    }
                );
                NetworkRequest.put(
                    "/api/list-management/",
                    {
                        body: restaurantsToSubmit
                    },
                    {
                        query: "restaurant",
                        listType: this.props.match.params.id
                    }
                );
            });
        }
    };

    render() {
        return (
            <>
                <LoginCommandBar
                    redirect={this.redirect}
                    showResultsButton/>
                <Stack
                    gap={20}
                    className="container">
                    <Text
                        as="h1"
                        variant="mega">
                        {["", "Favorites", "To Explore", "Do Not Show", "Grocery List"][this.props.match.params.id]}
                    </Text>
                    {
                        this.state.unauthorized === true
                            ? <Text variant="xxLarge">You must be logged in to view saved lists.</Text>
                            : (
                                <>
                                    {
                                        parseInt(this.props.match.params.id) === ListType.GROCERY_LIST
                                            ? (
                                                <div>
                                                    {
                                                        this.state.groceryItems && this.state.groceryItems.map((item, index) => {
                                                            return <GroceryItem key={index} {...item}/>;
                                                        })
                                                    }
                                                </div>
                                            )
                                            : (
                                                <Stack.Item>
                                                    <DragDropContext onDragEnd={this.onDragEnd}>
                                                        {
                                                            this.state.listItems === undefined
                                                                ? (
                                                                    <Text>Loading...</Text>
                                                                )
                                                                : (
                                                                    <Droppable droppableId="droppable-id">
                                                                        {
                                                                            provided => (
                                                                                <div
                                                                                    {...provided.droppableProps}
                                                                                    ref={provided.innerRef}>
                                                                                    {
                                                                                        this.state.listItems.map((item, index) => (
                                                                                            <Draggable
                                                                                                draggableId={`draggable-${index}`}
                                                                                                index={index}
                                                                                                key={index}>
                                                                                                {
                                                                                                    provided =>
                                                                                                        item.hasOwnProperty("restaurantID")
                                                                                                            ? (
                                                                                                                <RestaurantResult
                                                                                                                    redirect={this.redirect}
                                                                                                                    restaurant={item}
                                                                                                                    innerId={`draggable-${index}`}
                                                                                                                    innerRef={provided.innerRef}
                                                                                                                    draggableProps={provided.draggableProps}
                                                                                                                    dragHandleProps={provided.dragHandleProps}/>
                                                                                                            )
                                                                                                            : (
                                                                                                                <RecipeResult
                                                                                                                    recipe={item}
                                                                                                                    redirect={this.redirect}
                                                                                                                    innerId={`draggable-${index}`}
                                                                                                                    innerRef={provided.innerRef}
                                                                                                                    draggableProps={provided.draggableProps}
                                                                                                                    dragHandleProps={provided.dragHandleProps}/>
                                                                                                            )
                                                                                                }
                                                                                            </Draggable>
                                                                                        ))
                                                                                    }
                                                                                    {provided.placeholder}
                                                                                </div>
                                                                            )
                                                                        }
                                                                    </Droppable>
                                                                )
                                                        }
                                                    </DragDropContext>
                                                </Stack.Item>
                                            )
                                    }
                                </>
                            )
                    }
                </Stack>
                <div
                    style={{
                        background: "white",
                        height: "2rem"
                    }}
                    id="bottom-of-page">
                </div>
            </>
        );
    }
}

class GroceryItem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            checked: props.checked,
            isDeleted: false
        };
    }

    _toggleCheck = () => {
        this.setState(prevState => {
            prevState.checked = !prevState.checked;
            return {
                checked: prevState.checked
            };
        }, () => {
            NetworkRequest.put(
                "/api/grocery-list/",
                {
                    groceryID: this.props.groceryID,
                    checkList: this.state.checked
                }
            );
        });
    };

    _delete = () => {
        NetworkRequest.delete(
            "/api/grocery-list/",
            {recipeID: this.props.groceryID}
        )
            .then(() => {this.setState({isDeleted: true})})
    };

    render() {
        if (this.state.isDeleted) {
            return null;
        }

        return (
            <div style={{
                width: "100%",
                display: "flex",
                justifyContent: "space-between"
            }}>
                <Checkbox
                    checked={this.state.checked}
                    label={`${Math.ceil(this.props.amount).toFixed(0)} ${this.props.unit} ${this.props.ingredients}`}
                    onChange={this._toggleCheck}
                    styles={() => {
                        return {
                            root: {
                                marginTop: "10px"
                            }
                        }
                    }}/>
                <IconButton
                    iconProps={{ iconName: "ChromeClose" }}
                    onClick={this._delete}/>
            </div>
        )
    }
}

ListManagementPage.propTypes = {
    match: PropTypes.shape({
        params: PropTypes.shape({
            id: PropTypes.string
        })
    })
};