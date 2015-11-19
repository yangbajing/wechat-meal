import 'babel-core/polyfill';
import React from 'react';
import Router from 'react-router';
import {Provider} from 'react-redux';
import configureStore from './store/configureStore'

import appRoutes from './routes/appRoutes';

const store = configureStore();

Router.run(appRoutes, Router.HistoryLocation, (Handler, routerState) => {
    React.render(
        <Provider store={store}>
            {() => <Handler routerState={routerState}/>}
        </Provider>,
        document.getElementById('root')
    )
});
