import React from "react";
import PropTypes from "prop-types";
import {Rating, Stack, Text} from "office-ui-fabric-react";
import {mergeStyleSets} from "office-ui-fabric-react/lib/Styling"

export default class RestaurantResult extends React.PureComponent {
    render() {
        const styles = mergeStyleSets({
            priceBoxContainer: {
                borderLeft: "1px solid black",
                minWidth: "4rem"
            },
            priceBox: {
                height: "100%",
                width: "100%"
            }
        });

        return (
            <div
                id={this.props.innerId}
                ref={this.props.innerRef}
                {...this.props.draggableProps}
                {...this.props.dragHandleProps}>
                <Stack
                    className="restaurant-row result-row ms-borderColor-themePrimary ms-bgColor-white ms-bgColor-neutralLight--hover"
                    horizontal
                    onClick={() => this.props.redirect(`/restaurant/${this.props.restaurant.placeID}`)}>
                    <Stack.Item grow>
                        <span className="ms-fontSize-l ms-fontColor-themePrimary ms-fontWeight-semibold">
                            {this.props.restaurant.name}
                        </span>
                        <Rating
                            min={0}
                            max={5}
                            rating={this.props.restaurant.stars}
                            readOnly/>
                        <Text>
                            {`${this.props.restaurant.address} (${this.props.restaurant.driveTime})`}
                        </Text>
                    </Stack.Item>
                    <Stack.Item
                        className={styles.priceBoxContainer}>
                        <Stack
                            className={styles.priceBox}
                            horizontalAlign="center"
                            verticalAlign="center">
                            {"$".repeat(this.props.restaurant.price + 1)}
                        </Stack>
                    </Stack.Item>
                </Stack>
            </div>
        );
    }
}

RestaurantResult.propTypes = {
    innerId: PropTypes.string,
    redirect: PropTypes.func.isRequired,
    restaurant: PropTypes.shape({
        address: PropTypes.string,
        driveTime: PropTypes.string,
        name: PropTypes.string,
        placeID: PropTypes.string,
        price: PropTypes.number,
        stars: PropTypes.number
    }).isRequired
};