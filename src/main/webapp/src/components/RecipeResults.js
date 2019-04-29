import React from "react";
import PropTypes from "prop-types";
import {Shimmer} from "office-ui-fabric-react";
import RecipeResult from "./RecipeResult";

export default class RecipeResults extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            recipes: props.recipes
        };
    }

    componentDidUpdate(prevProps) {
        if (JSON.stringify(prevProps.recipes) !== JSON.stringify(this.props.recipes)) {
            this.setState({
                recipes: this.props.recipes
            });
        }
    }

    render() {
        if (this.state.recipes === null) {
            return null;
        }

        return (
            <>
                {
                    this.state.recipes !== undefined
                        ? (
                            <>
                                {
                                    this.state.recipes.map((item, index) => (
                                        <RecipeResult
                                            key={index}
                                            recipe={item}
                                            redirect={this.props.redirect}/>
                                    ))
                                }
                            </>
                        )
                        : (
                            <div className="result-row ms-borderColor-themePrimary">
                                <Shimmer/>
                                <div style={{margin: "0.5rem 0"}}>
                                    <Shimmer width="20%"/>
                                </div>
                                <Shimmer width="30%"/>
                            </div>
                        )
                }
            </>
        );
    }
}

RecipeResults.propTypes = {
    recipes: PropTypes.arrayOf(
        PropTypes.shape({
            cook_time: PropTypes.string,
            name: PropTypes.string,
            prep_time: PropTypes.string,
            rating: PropTypes.number,
            recipe_url: PropTypes.string
        })
    ),
    redirect: PropTypes.func.isRequired,
    innerRef: PropTypes.object
};