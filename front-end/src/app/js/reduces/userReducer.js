import Immutable from 'immutable';

import {DO_SIGNIN, USER_INFO} from '../actions/userActions';

export default function (state = {}, action = {}) {
    switch (action.type) {
        case DO_SIGNIN:
            return Immutable.fromJS(action.data);
        case USER_INFO:
            return Immutable.fromJS(action.data);
        default:
            return Immutable.fromJS(state);
    }
}
