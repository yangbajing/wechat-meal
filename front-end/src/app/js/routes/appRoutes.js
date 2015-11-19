import {Route, NotFoundRoute, Redirect, DefaultRoute} from 'react-router';

import App from '../components/App';
import Main from '../components/Main';

let routes = (
    <Route path="/admin" handler={App}>
        <DefaultRoute name="adminIndex" handler={Main}/>
    </Route>
);

export default routes;
