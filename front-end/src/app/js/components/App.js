import React, { Component } from 'react';
import {RouteHandler} from 'react-router';
import {connect} from 'react-redux';

class App extends Component {
    render() {
        return (
            <div>
                <h1>App</h1>
                <RouteHandler {...this.props}/>
            </div>
        );
    }
}

function mapStateToProps(state) {
    return {

    };
}

function mapDispatchToProps(dispatch) {
    return {
        signOut: () => dispatch(signOut())
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(App);
