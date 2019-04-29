import React from "react";
import PropTypes from "prop-types";
import {Shimmer, Text} from "office-ui-fabric-react";
import RestaurantResult from "./RestaurantResult";

export default class RestaurantResults extends React.Component {
    render() {
        let resultsToDisplay;
        if (this.props.restaurants === null) {
            return null;
        }
        else if (this.props.restaurants === undefined) {
            resultsToDisplay = (
                <div className="result-row ms-borderColor-themePrimary">
                    <Shimmer/>
                    <div style={{margin: "0.5rem 0"}}>
                        <Shimmer width="20%"/>
                    </div>
                    <Shimmer width="30%"/>
                </div>
            );
        } else if (this.props.restaurants.length === 0) {
            if (this.props.resultsPage) {
                resultsToDisplay = (
                    <Text>No restaurants found</Text>
                );
            }
            else {
                return null;
            }
        } else {
            resultsToDisplay = (
                this.props.restaurants.map((item, index) => (
                    <RestaurantResult
                        key={index}
                        redirect={this.props.redirect}
                        restaurant={item}/>
                ))
            )
        }
        return (
            <div>
                {resultsToDisplay}
            </div>
        );
    }
}

RestaurantResults.propTypes = {
    restaurants: PropTypes.arrayOf(
        PropTypes.shape({
            address: PropTypes.string,
            driveTime: PropTypes.string,
            name: PropTypes.string,
            placeID: PropTypes.string,
            price: PropTypes.number,
            stars: PropTypes.number,
        })
    ),
    resultsPage: PropTypes.bool,
    redirect: PropTypes.func.isRequired
};