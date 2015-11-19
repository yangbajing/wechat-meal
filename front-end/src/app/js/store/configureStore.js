import {createStore, applyMiddleware, compose} from 'redux';
import ReduxThunk from 'redux-thunk';
import rootReducer from '../reduces';
import ReduxLogger from 'redux-logger';
//import {devTools,PersistState} from 'redux-devtools';

const __DEV__ = true;

const logger = ReduxLogger({
    predicate: (getState, action) => __DEV__
});

const finalCreateStore = compose(
    applyMiddleware(ReduxThunk, logger),
    //devTools(),
    createStore
);

export default function configureStore(initialState) {
    return finalCreateStore(rootReducer, initialState);
}
