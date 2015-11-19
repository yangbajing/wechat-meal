import React from 'react';
import {RouteHandler} from 'react-router';
import {connect} from 'react-redux';

import {signIn} from '../../actions/userActions';

class SignApp extends React.Component {
    render() {
        return (
            <div>
                <RouteHandler {...this.props}/>
            </div>
        );
    }
}

function mapStateToProps(state) {
    return {
        data: {
            user: state.user
        }
    };
}

function mapDispatchToProps(dispatch) {
    return {
        action: {
            signIn: params => dispatch(signIn(params))
        }
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(SignApp);
