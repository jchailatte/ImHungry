import React from "react";
import PropTypes from "prop-types";
import {CommandBar} from "office-ui-fabric-react";
import BackToResults from "../util/BackToResults";
import ListType from "../util/ListType";

export default class DetailPageCommandBar extends React.PureComponent {
    get items() {
        const items = [
            {
                key: "groceries",
                name: "Grocery List",
                iconProps: {
                    iconName: "ShoppingCart"
                },
                id: "grocery-button",
                onClick: () => this.props.redirect(`/list-management/${ListType.GROCERY_LIST}`)
            },
            {
                key: "manage",
                name: "Manage List",
                iconProps: {
                    iconName: "BulletedList2"
                },
                id: "manage-list-button",
                subMenuProps: {
                    items: [
                        {
                            key: "favorite",
                            name: "Favorites",
                            iconProps: {
                                iconName: "FavoriteStar"
                            },
                            id: "favorite-button",
                            onClick: () => this.props.redirect(`/list-management/${ListType.FAVORITE}`)
                        },
                        {
                            key: "to-explore",
                            name: "To Explore",
                            iconProps: {
                                iconName: "Cycling"
                            },
                            onClick: () => this.props.redirect(`/list-management/${ListType.TO_EXPLORE}`)
                        },
                        {
                            key: "hide",
                            name: "Do Not Show",
                            iconProps: {
                                iconName: "Hide"
                            },
                            onClick: () => this.props.redirect(`/list-management/${ListType.DO_NOT_SHOW}`)
                        }
                    ]
                }
            }
        ];

        if (this.props.showResultsButton) {
            items.push({
                key: "results",
                name: "Back to Results",
                iconProps: {
                    iconName: "Articles"
                },
                id: "results-page-button",
                onClick: () => BackToResults.navigateResultsPage(this.props.redirect)
            });
        }

        items.push({
            key: "search",
            name: "Search",
            iconProps: {
                iconName: "Search"
            },
            id: "back-to-search",
            onClick: () => this.props.redirect("/")
        });

        items.reverse();

        return items;
    }

    render() {
        return (
            <CommandBar
                items={this.items}
                farItems={[this.props.loginItem]}/>
        );
    }
}

DetailPageCommandBar.propTypes = {
    loginItem: PropTypes.object.isRequired,
    redirect: PropTypes.func.isRequired,
    showResultsButton: PropTypes.bool
};