import React from "react";
import PropTypes from "prop-types";
import {Rating, Text} from "office-ui-fabric-react";

export default class RecipeResult extends React.PureComponent {
    render() {
        return (
            <div
                className="recipe-row result-row ms-borderColor-themePrimary ms-bgColor-white ms-bgColor-neutralLight--hover"
                id={this.props.innerId}
                onClick={() => this.props.redirect(`/recipe/?recipeURL=${this.props.recipe.recipe_url}`)}
                ref={this.props.innerRef}
                {...this.props.draggableProps}
                {...this.props.dragHandleProps}>
                <span
                    className="ms-fontSize-l ms-fontColor-themePrimary ms-fontWeight-semibold">
                    {this.props.recipe.name}
                </span>
                <Rating
                    min={0}
                    max={5}
                    rating={this.props.recipe.rating}
                    readOnly/>
                <Text>
                    Prep: {this.props.recipe.prep_time}
                </Text>
                <Text className="cook-time">
                    Cook: {this.props.recipe.cook_time}
                </Text>
            </div>
        )
    }
}

RecipeResult.propTypes = {
    id: PropTypes.string,
    recipe: PropTypes.shape({
        cook_time: PropTypes.string,
        name: PropTypes.string,
        prep_time: PropTypes.string,
        rating: PropTypes.number,
        recipe_urL: PropTypes.string
    }).isRequired,
    redirect: PropTypes.func.isRequired,
};