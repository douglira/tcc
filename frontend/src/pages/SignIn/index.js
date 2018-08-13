import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Redirect } from 'react-router-dom';
import { withStyles } from '@material-ui/core/styles';
import { Input, InputLabel, FormControl } from '@material-ui/core';
// import { Formik } from 'formik';

import { connect } from 'react-redux';
import { Creators as UserActions } from '~/store/ducks/user';

import { Container, Content, MaterialUI } from './styles';

class SignIn extends Component {
  static propTypes = {
    classes: PropTypes.shape().isRequired,
    location: PropTypes.shape().isRequired,
    isAuthenticated: PropTypes.bool.isRequired,
    signinRequest: PropTypes.func.isRequired,
    history: PropTypes.shape({
      redirect: PropTypes.func,
    }).isRequired,
  };

  state = {
    form: {
      email: '',
      password: '',
    },
  };

  onFormChange = fieldName => (event) => {
    const { form: stateForm } = this.state;
    const form = { ...stateForm };

    form[fieldName] = event.target.value;
    this.setState({ form });
  };

  onSignIn = (event) => {
    event.preventDefault();

    const { form } = this.state;
    const { signinRequest, history } = this.props;

    signinRequest(form, () => {
      history.redirect('/admin/home');
    });
  };

  render() {
    const { classes, isAuthenticated, location } = this.props;
    const { form } = this.state;

    const { from } = location.state || { from: { pathname: '/admin/home' } };

    if (isAuthenticated) {
      return <Redirect to={from.pathname} />;
    }

    return (
      <Container>
        <Content>
          <h1>Login</h1>
          <form onSubmit={this.onSignIn}>
            <FormControl className={classes.formControl}>
              <InputLabel
                className={classes.inputLabel}
                FormLabelClasses={{
                  root: classes.cssLabel,
                  focused: classes.cssFocused,
                }}
                htmlFor="email"
              >
                Email
              </InputLabel>
              <Input
                className={classes.inputText}
                classes={{
                  underline: classes.cssUnderline,
                }}
                autoFocus
                id="email"
                type="email"
                placeholder="Email cadastrado"
                onChange={this.onFormChange('email')}
                value={form.email}
              />
            </FormControl>
            <FormControl className={classes.formControl}>
              <InputLabel
                className={classes.inputLabel}
                FormLabelClasses={{
                  root: classes.cssLabel,
                  focused: classes.cssFocused,
                }}
                htmlFor="password"
              >
                Senha
              </InputLabel>
              <Input
                className={classes.inputText}
                classes={{
                  underline: classes.cssUnderline,
                }}
                id="password"
                type="password"
                placeholder="Sua senha de acesso"
                onChange={this.onFormChange('password')}
                value={form.password}
              />
            </FormControl>
            <button type="submit">Entrar</button>
          </form>
        </Content>
      </Container>
    );
  }
}

const mapStateToProps = ({ user }) => ({
  isAuthenticated: user.isAuthenticated,
});

const mapDispatchToProps = dispatch => ({
  signinRequest: credentials => dispatch(UserActions.signinRequest(credentials)),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(withStyles(MaterialUI)(SignIn));
