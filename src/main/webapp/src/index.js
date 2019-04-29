import "@babel/polyfill";

import React from "react";
import ReactDOM from "react-dom";
import {BrowserRouter as Router, Redirect, Route, Link, Switch} from "react-router-dom";
import {initializeIcons, PrimaryButton, Stack, Text} from "office-ui-fabric-react";
import SearchPage from "./pages/search";
import ResultsPage from "./pages/results";
import ListManagementPage from "./pages/list-management";
import RecipePage from "./pages/recipe";
import RestaurantPage from "./pages/restaurant";

initializeIcons();

class App extends React.Component {
    render() {
        return (
            <Router>
                <Switch>
                    <Route path="/" exact component={SearchPage}/>
                    <PrivateRoute path="/results" component={ResultsPage}/>
                    <PrivateRoute path="/list-management/:id" component={ListManagementPage}/>
                    <PrivateRoute path="/recipe/" component={RecipePage}/>
                    <PrivateRoute path="/restaurant/:placeID" component={RestaurantPage}/>
                    <Route component={NotFound}/>
                </Switch>
            </Router>
        );
    }
}

function PrivateRoute({component: Component, ...rest}) {
    const hasToken = !!window.localStorage.getItem("token");
    return (
        <Route
            {...rest}
            render={
                props =>
                    hasToken
                        ? <Component {...props}/>
                        : <Redirect to={{pathname: "/"}}/>
            }
        />
    )
}

function NotFound() {
    return (
        <Stack
            style={{height: "100vh"}}
            horizontalAlign="center"
            verticalAlign="center">
            <Text variant="mega">
                Page Not Found
            </Text>
            <Link to={"/"}>
                <PrimaryButton text="Home Page"/>
            </Link>
        </Stack>
    );
}

ReactDOM.render(
    <App/>,
    document.getElementById("app")
);