package models;

// Represents the types of lists that a restaurant or recipe can be a part of.
// NONE: not in any list
// FAVORITE: in the Favorites list
// TO_EXPLORE: in the To Explore list
// DO_NOT_SHOW: in a list of items that should not appear in results
public enum ListType {
    NONE, FAVORITE, TO_EXPLORE, DO_NOT_SHOW
}
