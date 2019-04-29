import React from "react";
import PropTypes from "prop-types";
import {CollageImage} from "./CollageImage";
import {mergeStyleSets} from "office-ui-fabric-react/lib/Styling";

export default class ImageCollage extends React.PureComponent {
    render() {
        const styles = mergeStyleSets({
            row: {
                display: "flex",
                height: "50%"
            }
        });

        // Populates image with 10 placeholder images should there not be
        // a sufficient amount of results. Also ensures only 10 images are displayed.
        const images = this.props.images ? [...this.props.images] : [];
        while (images.length < 10) {
            images.push("https://via.placeholder.com/256x256");
        }
        if (images.length < 10) {
            images.slice(0, 10);
        }

        return (
            <div style={{
                position: "absolute",
                height: "100%",
                width: "100%"
            }}>
                <div className={styles.row}>
                    <CollageImage image={images[0]}/>
                    <CollageImage image={images[1]}/>
                    <CollageImage image={images[2]}/>
                    <CollageImage image={images[3]}/>
                    <CollageImage image={images[4]}/>
                </div>
                <div className={styles.row}>
                    <CollageImage image={images[5]}/>
                    <CollageImage image={images[6]}/>
                    <CollageImage image={images[7]}/>
                    <CollageImage image={images[8]}/>
                    <CollageImage image={images[9]}/>
                </div>
            </div>
        );
    }
}

ImageCollage.propTypes = {
    images: PropTypes.arrayOf(PropTypes.string).isRequired,
    innerStyle: PropTypes.object
};