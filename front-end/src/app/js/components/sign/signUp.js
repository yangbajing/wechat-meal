import React, { Component } from 'react';

const CAPTCHA_URI = '/api/auth/captcha';

class SingUp extends Component {
    constructor(props) {
        super(props);
        this.state = {
            formValue: {
                account: '',
                password: '',
                password2: '',
                captcha: ''
            },
            formValidation: {
                account: '',
                password: '',
                password2: '',
                captcha: ''
            }
        };
    }

    isFormDisabled() {
        const formValidation = this.state.formValidation;
        return (formValidation.account.length + formValidation.password.length + formValidation.password2.length +
            formValidation.captcha.length) > 0;
    }

    onSignUp(e) {
        e.preventDefault();
        let params = this.state.formValue;

        const disabled = this.isFormDisabled();
        if (!disabled) {
            this.props.action.signUp(params);
        }
    }

    onChange(e) {
        let params = this.state.formValue;
        params[e.target.name] = e.target.value;

        //let formValidation = {};
        let formValidation = this.state.formValidation;
        formValidation[e.target.name] = '';

        if (params.account.length < 3) {
            formValidation.account = '账号名不得少于3位';
        }
        if (params.password.length < 6) {
            formValidation.password = '密码不能少于6位';
        }
        if (params.password !== params.password2) {
            formValidation.password2 = '两次密码不匹配';
        }
        if (params.captcha.length !== 4) {
            formValidation.captcha = '请输入图片验证码';
        }

        //console.log(formValidation);
        this.setState({formValue: params, formValidation: formValidation});
    }

    onEnterToSignUp(e) {
        if (e.keyCode === 13) {
            this.onSignUp(e);
        }
    }

    onRefreshCaptcha(e) {
        e.preventDefault();
        e.target.src = CAPTCHA_URI + '?' + (new Date().getTime());
    }

    render() {
        const disabled = this.isFormDisabled();
        console.log(JSON.stringify(this.state.formValidation));
        console.log('disabled: ' + disabled);
        return (
            <div>
                <form role="form">
                    <div className="form-group">
                        <input type="text" className="form-control" name="account" placeholder="邮箱"
                               onChange={this.onChange.bind(this)}/>
                        <span className="text-danger text-right">{this.state.formValidation.account}</span>
                    </div>
                    <div className="form-group">
                        <input type="password" className="form-control" name="password" placeholder="密码"
                               onChange={this.onChange.bind(this)}/>
                        <span className="text-danger text-right">{this.state.formValidation.password}</span>
                    </div>
                    <div className="form-group">
                        <input type="password" className="form-control" name="password2" placeholder="确认密码"
                               onChange={this.onChange.bind(this)}/>
                        <span className="text-danger text-right">{this.state.formValidation.password2}</span>
                    </div>
                    <div className="form-group">
                        <div className="row">
                            <a href="javascript:;" onClick={this.onRefreshCaptcha.bind(this)}>
                                <img src={CAPTCHA_URI} className="col-xs-6"/>
                            </a>

                            <div className="col-xs-6">
                                <label className="sr-only">验证码</label>
                                <input tag="3" className="form-control" name="captcha" placeholder="点击图片刷新验证码"
                                       onKeyUp={this.onEnterToSignUp.bind(this)} onChange={this.onChange.bind(this)}/>
                                <span className="text-danger text-right">{this.state.formValidation.captcha}</span>
                            </div>
                        </div>
                    </div>
                    <div className="form-group">
                        <button type="button" className="btn btn-primary btn-block" disabled={disabled}
                                onClick={this.onSignUp.bind(this)}>
                            注册
                        </button>
                    </div>
                </form>
            </div>
        );
    }
}

export default SingUp;
