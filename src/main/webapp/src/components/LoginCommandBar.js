import React from "react";
import PropTypes from "prop-types";
import DetailPageCommandBar from "./DetailPageCommandBar";
import ResultsCommandBar from "./ResultsCommandBar";
import {
    ActionButton,
    CommandBar, CommandBarButton,
    Dialog, DialogFooter,
    MessageBar, MessageBarType,
    Persona, PersonaSize,
    PrimaryButton,
    TextField
} from "office-ui-fabric-react";
import NetworkRequest from "../util/NetworkRequest";

export default class LoginCommandBar extends React.Component {
    constructor(props) {
        super(props);

        const user = this._parseUserJwt(
            window.localStorage.getItem("token")
        );

        this.state = {
            isLogin: true,
            messageBarMessage: "",
            password: "",
            showDialog: false,
            showError: false,
            user,
            username: "",
        };
    }

    get loginItem() {
        if (this.state.user) {
            return {
                key: "persona",
                onRender: () => {
                    return (
                        <div
                            id="persona"
                            style={{
                                display: "flex"
                            }}>
                            <CommandBarButton
                                menuProps={{
                                    items: [
                                        {
                                            key: "logout",
                                            name: "Logout",
                                            iconProps: {
                                                iconName: "FollowUser"
                                            },
                                            id: "logout-button",
                                            onClick: this.logout
                                        }
                                    ]
                                }}>
                                <Persona
                                    imageIntialis={this.state.user.username[0]}
                                    size={PersonaSize.size24}
                                    text={this.state.user.username}/>
                            </CommandBarButton>
                        </div>
                    )
                }
            }
        } else {
            return {
                key: "login",
                name: "Login",
                iconProps: {
                    iconName: "Contact"
                },
                id: "login-button",
                onClick: () => {
                    this.setState({showDialog: true})
                }
            }
        }
    }

    /**
     * Login form submit handler.
     * Saves JWT to localStorage if successful.
     *
     * @returns {Promise<void>}
     */
    login = async () => {
        if (this.state.username === "" || this.state.password === "") {
            this.setState({
                showError: true
            });
            return;
        }

        try {
            const response = await NetworkRequest.post(
                "/api/login/",
                {
                    username: this.state.username,
                    password: this.state.password
                }
            );
            if (response.ok) {
                const data = await response.json();
                const {token} = data;
                window.localStorage.setItem("token", token);
                this.setState({
                    showDialog: false,
                    user: this._parseUserJwt(token)
                }, () => window.location.reload());
            } else {
                this.setState({
                    messageBarMessage: "Username or password is invalid"
                });
            }
        } catch (exception) {
            console.error("Fatal error when trying to login", exception);
        }
    };

    /**
     * Clears user from local storage and reloads page
     */
    logout = () => {
        window.localStorage.removeItem("token");
        window.location.reload();
    };

    /**
     * Text change handler for username / password fields
     *
     * @param event
     */
    onChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    /**
     * Registration form submit handler.
     * Saves JWT to localStorage if successful.
     *
     * @returns {Promise<void>}
     */
    register = async () => {
        if (this.state.username === "" || this.state.password === "" || !this._validatePassword(this.state.password)) {
            this.setState({
                showError: true
            });
            return;
        }

        try {
            const response = await NetworkRequest.post(
                "/api/register/",
                {
                    username: this.state.username,
                    password: this.state.password
                }
            );

            if (response.ok) {
                const data = await response.json();
                const {token} = data;
                window.localStorage.setItem("token", token);
                this.setState({
                    showDialog: false,
                    user: this._parseUserJwt(token)
                });
            } else {
                this.setState({
                    messageBarMessage: "An account with that username already exists"
                });
            }
        } catch (exception) {
            console.error("Fatal error when trying to register", exception);
        }
    };

    /**
     * Closes login / register modal
     *
     * @private
     */
    _closeDialog = () => {
        this.setState({
            showDialog: false
        });
    };

    /**
     * Parses user JSON from JWT body (payload)
     *
     * @param jwt
     * @returns {{userID: number, username: string} | null}
     * @private
     */
    _parseUserJwt = (jwt) => {
        if (!jwt) {
            return null;
        }

        try {
            const base64Url = jwt.split(".")[1];
            const base64 = base64Url.replace("-", "+").replace("_", "/");
            return JSON.parse(window.atob(base64));
        } catch {
            return null;
        }
    };

    /**
     * When toggling between login / register forms, reset the error messages
     * @private
     */
    _toggleIsLogin = () => {
        this.setState(prevState => {
            return {
                isLogin: !prevState.isLogin,
                messageBoxMessage: "",
                showError: false,
            }
        });
    };

    /**
     * Ensure password meets security requirements of 8+ characters with a number
     *
     * @param password
     * @returns {boolean} true if valid
     * @private
     */
    _validatePassword = (password) => {
        return password.length >= 8 || /\d/.test(password);
    };

    render() {
        let commandBar;
        if (this.props.home) {
            commandBar = (
                <CommandBar farItems={[this.loginItem]}/>
            );
        } else if (this.props.showResultsButton) {
            commandBar = (
                <ResultsCommandBar
                    loginItem={this.loginItem}
                    redirect={this.props.redirect}
                    showResultsButton={this.props.showResultsButton}/>
            );
        } else {
            commandBar = (
                <DetailPageCommandBar
                    addToGroceryList={this.props.addToGroceryList}
                    addToList={this.props.addToList}
                    loginItem={this.loginItem}
                    redirect={this.props.redirect}/>
            );
        }

        let passwordErrorMessage = "";
        if (this.state.showError) {
            if (this.state.password === "") {
                passwordErrorMessage = "Password may not be blank";
            } else if (!this._validatePassword(this.state.password)) {
                passwordErrorMessage = "Password is too weak";
            }
        }

        return (
            <>
                {commandBar}
                <Dialog
                    hidden={!this.state.showDialog}
                    onDismiss={this._closeDialog}
                    dialogContentProps={{
                        title: this.state.isLogin ? "Login" : "Register"
                    }}>
                    {
                        this.state.messageBarMessage &&
                        <MessageBar messageBarType={MessageBarType.error}>
                            {this.state.messageBarMessage}
                        </MessageBar>
                    }
                    <TextField
                        errorMessage={
                            this.state.showError && this.state.username === ""
                                ? "Username may not be blank"
                                : ""
                        }
                        id="username"
                        label="Username"
                        name="username"
                        onChange={this.onChange}/>
                    <TextField
                        description={this.state.isLogin ? "" : "At least 8 characters with 1 number"}
                        errorMessage={passwordErrorMessage}
                        id="password"
                        label="Password"
                        name="password"
                        onChange={this.onChange}
                        type="password"/>
                    {
                        this.state.isLogin
                            ? (
                                <ActionButton
                                    iconProps={{iconName: "AddFriend"}}
                                    id="sign-up-button"
                                    onClick={this._toggleIsLogin}>
                                    Create account
                                </ActionButton>
                            )
                            : (
                                <ActionButton
                                    iconProps={{iconName: "Contact"}}
                                    id="sign-up-button"
                                    onClick={this._toggleIsLogin}>
                                    Sign in
                                </ActionButton>
                            )
                    }
                    <DialogFooter>
                        {
                            this.state.isLogin
                                ? <PrimaryButton
                                    id="login-confirm-button"
                                    onClick={this.login}
                                    text="Login"/>
                                : <PrimaryButton
                                    id="sign-up-confirm-button"
                                    onClick={this.register}
                                    text="Register"/>
                        }
                    </DialogFooter>
                </Dialog>
            </>
        );
    }
}

LoginCommandBar.propTypes = {
    addToGroceryList: PropTypes.func,
    addToList: PropTypes.func,
    home: PropTypes.bool,
    redirect: PropTypes.func.isRequired,
    showResultsButton: PropTypes.bool
};