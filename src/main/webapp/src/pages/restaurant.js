import React from "react";
import {DefaultButton, Dialog, DialogFooter, Icon, Link, Stack, Text} from "office-ui-fabric-react";
import {withRouter} from "react-router-dom";
import NetworkRequest from "../util/NetworkRequest";
import LoginCommandBar from "../components/LoginCommandBar";

@withRouter
export default class RestaurantPage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            data: undefined,
            showAuthorizationDialog: false
        };

        document.title = "Restaurant";
    }

    get drivingDirectionsLink() {
        if (this.state.data === undefined) {
            return "";
        }
        return `https://www.google.com/maps/dir/?api=1&origin=Tommy+Trojan&destination=${this.state.data.name}&destination_place_id=${this.state.data.placeID}&travelmode=driving`
    }

    async componentDidMount() {
        try {
            const response = await NetworkRequest.get(
                "/api/restaurant/",
                {placeID: this.props.match.params.placeID}
            );
            if (response.ok) {
                const data = await response.json();
                this.setState({data});
            } else {
                console.error(response.status, "Could not get restaurant");
            }
        } catch (exception) {
            console.error("Fatal error when trying to get restaurant", exception);
        }
    }

    addToList = async (listType) => {
        try {
            const response = await NetworkRequest.post(
                "/api/restaurant/",
                {
                    placeID: this.props.match.params.placeID,
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
            console.error("Fatal error when trying to change restaurant list", exception);
        }
    };

    _dismissDialog = () => {
        this.setState({
            showAuthorizationDialog: false
        });
    };

    render() {
        if (this.state.data === undefined) {
            return <Text>Loading...</Text>
        }

        return (
            <>
                <LoginCommandBar
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
                    gap={20}
                    className="container">
                    <Text
                        style={{color:"#0078d4"}}
                        as="h1"
                        variant="mega">
                        {this.state.data.name}
                    </Text>
                    <Text
                        variant="xLarge">
                        <Icon iconName="MapPin"/>
                        {" "}
                        <Link
                            id="address"
                            href={this.drivingDirectionsLink}>
                            {this.state.data.address}
                        </Link>
                    </Text>
                    <Text
                        variant="xLarge">
                        <Icon iconName="Phone"/>
                        {" "}
                        {this.state.data.phoneNumber}
                    </Text>
                    <Text
                        variant="xLarge">
                        <Icon iconName="Link"/>
                        {" "}
                        <Link
                            id="url"
                            href={this.state.data.webURL}>
                            {this.state.data.webURL}
                        </Link>
                    </Text>
                </Stack>
            </>
        );
    }
}