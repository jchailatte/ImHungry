export default class BackToResults {
    /**
     * Navigates back to the results page. Since the user may navigate to another list
     * management type, it is not a simple "go back one step in history". Instead,
     * read from sessionStorage of the most recent query parameters and build
     * the search url from there.
     */
    static navigateResultsPage = (redirect) => {
        const query = window.sessionStorage.getItem("query");
        const numberOfResults = window.sessionStorage.getItem("numberOfResults");
        const radius = window.sessionStorage.getItem("radius");
        redirect(`/results/?query=${query}&numberOfResults=${numberOfResults}&radius=${radius}`);
    };
}