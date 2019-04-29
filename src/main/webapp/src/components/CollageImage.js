import React from "react";
import PropTypes from "prop-types";

/**
 * Displays image fit to center to fill div
 */
export function CollageImage(props) {
    return (
        <div
            className="collage-item"
            style={{background: `url('${props.image}')`}}>
        </div>
    );
}

CollageImage.propTypes = {
    image: PropTypes.string.isRequired
};