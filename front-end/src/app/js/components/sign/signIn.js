import React, { Component } from 'react';

class SingIn extends Component {
    constructor(props) {
        super(props);
        this.state = {
            formValue: {
                account: '',
                password: ''
            }
        };
    }

    componentWillReceiveProps(nextProps) {
        //if (nextProps.data.user.get('status') === 'ACTIVE') {
        //    window.location.herf = '/main/index';
        //} else if (nextProps.data.user.get('errorCode') === 401) {
        //    window.alert('请检查用户名或密码是否正确');
        //}
    }

    onSignIn(e) {
        e.preventDefault();
        let params = this.state.formValue;
        this.props.action.signIn(params);
    }

    onChange(e) {
        let value = this.state.formValue;
        value[e.target.name] = e.target.value;
        this.setState({formValue: value});
    }

    onEnterToSignIn(e) {
        if (e.keyCode === 13) {
            this.onSignIn(e);
        }
    }

    render() {
        return (
            <div>
                <form role="form">
                    <div className="form-group">
                        <label className="sr-only">账号(邮箱)</label>
                        <input tab="1" type="text" className="form-control" name="account" placeholder="邮箱"
                               onChange={this.onChange.bind(this)}/>
                    </div>
                    <div className="form-group">
                        <label className="sr-only">密码</label>
                        <input tab="2" type="password" className="form-control" name="password" placeholder="密码"
                               onChange={this.onChange.bind(this)}/>
                    </div>
                    <div className="form-group">
                        <button type="button" className="btn btn-primary btn-block" onClick={this.onSignIn.bind(this)}>
                            登录
                        </button>
                    </div>
                </form>
            </div>
        );
    }
}

export default SingIn;
