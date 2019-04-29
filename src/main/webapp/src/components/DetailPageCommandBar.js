import React from "react";
import PropTypes from "prop-types";
import {CommandBar} from "office-ui-fabric-react";
import ListType from "../util/ListType";
import BackToResults from "../util/BackToResults";

export default class DetailPageCommandBar extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            show: true
        };
    }

    /**
     * Hides command bar for printing
     */
    print = () => {
        this.setState({
            show: false
        });
    };

    render() {
        if (this.state.show) {
            return (
                <CommandBar
                    items={[
                        {
                            key: "results",
                            name: "Back to Results",
                            iconProps: {
                                iconName: "Articles"
                            },
                            id: "results-page-button",
                            onClick: () => BackToResults.navigateResultsPage(this.props.redirect)
                        },
                        {
                            key: "print",
                            name: "Print",
                            iconProps: {
                                iconName: "Print"
                            },
                            id: "printable-version-button",
                            onClick: this.print
                        },
                        {
                            key: "add",
                            name: "Add to List",
                            iconProps: {
                                iconName: "Add"
                            },
                            id: "add-to-list-button",
                            subMenuProps: {
                                items: [
                                    {
                                        key: "favorite",
                                        name: "Favorites",
                                        iconProps: {
                                            iconName: "FavoriteStar"
                                        },
                                        id: "add-to-favorite-button",
                                        onClick: () => this.props.addToList(ListType.FAVORITE)
                                    },
                                    {
                                        key: "to-explore",
                                        name: "To Explore",
                                        iconProps: {
                                            iconName: "Cycling"
                                        },
                                        id: "add-to-explore-button",
                                        onClick: () => this.props.addToList(ListType.TO_EXPLORE)
                                    },
                                    {
                                        key: "hide",
                                        name: "Do Not Show",
                                        iconProps: {
                                            iconName: "Hide"
                                        },
                                        id: "add-to-do-not-show-button",
                                        onClick: () => this.props.addToList(ListType.DO_NOT_SHOW)
                                    },
                                    {
                                        key: "groceries",
                                        name: "Grocery List",
                                        iconProps: {
                                            iconName: "ShoppingCart"
                                        },
                                        id: "add-to-grocery-list-button",
                                        onClick: () => this.props.addToGroceryList()
                                    }
                                ]
                            }
                        },
                        {
                            key: "groceries",
                            name: "Grocery List",
                            iconProps: {
                                iconName: "ShoppingCart"
                            },
                            id: "grocery-button",
                            onClick: () => this.props.redirect(`/list-management/${ListType.GROCERY_LIST}`)
                        }
                    ]}
                    farItems={[this.props.loginItem]}
                />
            );
        } else {
            return null;
        }
    }
}

DetailPageCommandBar.propTypes = {
    addToGroceryList: PropTypes.func.isRequired,
    addToList: PropTypes.func.isRequired,
    loginItem: PropTypes.object.isRequired,
    redirect: PropTypes.func.isRequired
};