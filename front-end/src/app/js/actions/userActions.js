import Http from 'superagent';

export const DO_SIGNIN = 'DO_SIGNIN';
export const DO_SIGNUP = 'DO_SIGNUP';
export const USER_INFO = 'USER_INFO';

function returnSignIn(data) {
    return {
        type: DO_SIGNIN,
        data: data
    };
}

function returnSignUp(data) {
    return {
        type: DO_SIGNUP,
        data: data
    };
}

function returnUser(data) {
    return {
        type: USER_INFO,
        data: data
    };
}

export function signIn(params) {
    return dispatch => {
        Http.post('/api/auth/signin')
            .send(params)
            .end((err, resp) => {
                dispatch(returnSignIn(resp.body));
            });
    };
}

export function signUp(params) {
    return dispatch => {
        Http.post('/api/auth/signup')
            .send(params)
            .end((err, resp) => {
                dispatch(returnSignUp(resp.body));
            })
    }
}

export function signOut() {
    return dispatch => {
        Http.del('/api/auth/signout')
            .end((err, resp) => {
                window.location.href = '/auth/signin';
            });
    };
}

export function getUser() {
    return dispatch => {
        Http.post('/api/user')
            .end((err, resp) => {
                dispatch(returnUser(resp.body));
            });
    };
}
