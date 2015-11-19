import React from 'react';
import Router, {Route, RouteHandler, HistoryLocation} from 'react-router';
import {Provider} from 'react-redux';

import configureStore from './store/configureStore';
import SignApp from './components/sign/signApp';
import SignIn from './components/sign/signIn';
import SignUp from './components/sign/signUp';

const store = configureStore();

let routes = (
    <Route name="auth" path="/auth" handler={SignApp}>
        <Route name="authSignIn" path="signin" handler={SignIn}/>
        <Route name="authSignUp" path="signup" handler={SignUp}/>
    </Route>
);

Router.run(routes, HistoryLocation, (Handler, routerState) => {
    React.render(
        <Provider store={store}>
            {() => <Handler routerState={routerState}/>}
        </Provider>,
        document.body);
});
