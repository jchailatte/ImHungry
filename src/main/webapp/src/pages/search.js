import React from "react";

import {
    ComboBox,
    PrimaryButton,
    SelectableOptionMenuItemType,
    Stack,
    Text,
    TextField,
    TooltipHost
} from "office-ui-fabric-react";
import {mergeStyleSets} from "office-ui-fabric-react/lib/Styling";
import {withRouter} from "react-router-dom";
import LoginCommandBar from "../components/LoginCommandBar";
import NetworkRequest from "../util/NetworkRequest";

@withRouter
export default class SearchPage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            clicked: false,
            emoji: "EmojiDisappointed",
            list: [],
            numberOfResults: 5,
            query: "",
            radius: 25,
        };

        document.title = "Search";
    }

    componentDidMount() {
        this.getSearchHistory();
    }

    /**
     * Checks that all fields are valid so that submission can proceed
     */
    get isValid() {
        return this.numberOfResultsIsValid && this.queryIsValid && this.radiusIsValid;
    }

    /**
     * Validates that number of results is a positive number
     */
    get numberOfResultsIsValid() {
        return this._isPositiveNumber(this.state.numberOfResults);
    }

    /**
     * Validates that query is non empty
     *
     * @returns {boolean}
     */
    get queryIsValid() {
        return this.state.query !== "";
    }

    /**
     * Validates that radius is a positive number
     */
    get radiusIsValid() {
        return this._isPositiveNumber(this.state.radius);
    }

    /**
     * Gets search history for the user
     *
     * @returns {Promise<void>}
     */
    getSearchHistory = async () => {
        try {
            const response = await NetworkRequest.get("/api/search-history/");
            if (response.ok) {
                const data = await response.json();
                this.setState({
                    list: data.map((item, index) => {
                        return {
                            key: index,
                            text: item
                        };
                    })
                });
            }
        }
        catch (exception) {
            console.error("Fatal error when getting search history", exception);
        }
    };

    /**
     * Handles change of search box (query) input
     *
     * @param event
     * @param option
     */
    handleComboChange = (event, option) => {
        if (option) {
            this.setState({query: option.text});
        } else {
            this.setState({query: event.target.value});
        }
    };

    /**
     * Handles change of field input
     *
     * @param event
     */
    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    /**
     * If the submit fields are valid, make the emoji happy and redirect to the results page
     */
    submit = () => {
        this.setState({
            clicked: true,
            emoji: this.isValid ? "Emoji" : "EmojiDisappointed"
        }, () => {
            if (this.isValid) {
                setTimeout(() => {
                    const radiusInMeters = this.state.radius * 1609.34;
                    const redirectLocation = `/results/?query=${this.state.query}&numberOfResults=${this.state.numberOfResults}&radius=${radiusInMeters}`;
                    this.props.history.push(redirectLocation);
                }, 500);
            }
        });
    };

        /**
     * Asserts that an inputted value (any type) is a number and is positive
     *
     * @param value any value, will be parsed to float
     * @returns {boolean} true if is positive number
     * @private
     */
    _isPositiveNumber = (value) => {
        let number;
        try {
            number = parseFloat(value);
            return number > 0;
        } catch {
            return false;
        }
    };

    render() {
        const styles = mergeStyleSets({
            commandBar: {
                position: "fixed",
                top: 0,
                width: "100%"
            },
            root: {
                background: "url('/static/img/home.jpg')",
                backgroundRepeat: "no-repeat",
                backgroundSize: "cover",
                height: "100vh"
            },
            searchContainer: {
                width: "50%",
                background: "rgba(255, 255, 255, 0.9)",
                padding: "2rem",
                color:"#0078d4"
            }
        });

        return (
            <>
                <div className={styles.commandBar}>
                    <LoginCommandBar
                        home
                        redirect={this.props.history.push}/>
                </div>
                <Stack
                    align="center"
                    className={styles.root}
                    horizontalAlign="center"
                    verticalAlign="center">
                    <Stack.Item className={styles.searchContainer}>
                        <Stack
                            gap={20}
                            horizontalAlign="center"
                            verticalAlign="center">
                            <Text variant="mega" block>I'm Hungry</Text>
                            <div className="ms-Grid" dir="ltr">
                                <div className="ms-Grid-row">
                                    <div className="ms-Grid-col ms-sm12 ms-md12 ms-lg6 ms-xl8">
                                        <ComboBox
                                            allowFreeform
                                            autoComplete="on"
                                            errorMessage={
                                                this.state.clicked && !this.queryIsValid
                                                    ? "Field may not be blank"
                                                    : ""
                                            }
                                            id="query"
                                            name="query"
                                            onChange={this.handleComboChange}
                                            options={[
                                                {
                                                    key: "SearchHistoryHeader",
                                                    text: "Search history",
                                                    itemType: SelectableOptionMenuItemType.Header
                                                },
                                                ...this.state.list
                                            ]}
                                            placeholder="Enter food"
                                            text={this.state.query}
                                            onItemClick = {this.submit}
                                            useComboBoxAsMenuWidth="on"/>
                                    </div>
                                    <div className="ms-Grid-col ms-sm12 ms-md6 ms-lg3 ms-xl2">
                                        <TooltipHost content="Number of items to show in results">
                                            <TextField
                                                id="number-of-results"
                                                name="numberOfResults"
                                                errorMessage={
                                                    this.state.clicked && !this.numberOfResultsIsValid
                                                        ? "Must be positive number"
                                                        : ""
                                                }
                                                onChange={this.handleChange}
                                                value={this.state.numberOfResults}/>
                                        </TooltipHost>
                                    </div>
                                    <div className="ms-Grid-col ms-sm12 ms-md6 ms-lg3 ms-xl2">
                                        <TooltipHost content="Radius from Tommy Trojan to search restaurants for">
                                            <TextField
                                                id="radius"
                                                name="radius"
                                                errorMessage={
                                                    this.state.clicked && !this.radiusIsValid
                                                        ? "Must be positive number"
                                                        : ""
                                                }
                                                onChange={this.handleChange}
                                                suffix="mi"
                                                value={this.state.radius}/>
                                        </TooltipHost>
                                    </div>
                                </div>
                            </div>
                            <PrimaryButton
                                iconProps={{
                                    iconName: this.state.emoji
                                }}
                                id="submit-button"
                                onClick={this.submit}
                                text="Feed Me!"/>
                        </Stack>
                    </Stack.Item>
                </Stack>
            </>
        )
    }
}