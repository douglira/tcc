import React from 'react';
import { Route, Switch, Redirect } from 'react-router-dom';

import { Container, Content } from './styles';

import FormNaturalPerson from './components/FormNaturalPerson';
import FormLegalPerson from './components/FormLegalPerson';

const SignUp = () => (
  <Container>
    <Content>
      <h1>
        <i className="fa fa-user-o fa-2x" />
        Cadastrar-se
      </h1>
      <p>Preencha o formulário abaixo</p>
      <Switch>
        <Route exact path="/signup/natural" component={FormNaturalPerson} />
        <Route exact path="/signup/legal" component={FormLegalPerson} />
        <Route render={() => <Redirect to="/signup/natural" />} />
      </Switch>
    </Content>
  </Container>
);

export default SignUp;
