import React from 'react';
import { Switch, Route } from 'react-router-dom';

import { Container } from './styles';

import HomePage from './pages/Home';

const AdminHome = () => (
  <Container>
    <Switch>
      <Route exact path="/admin/home" component={HomePage} />
    </Switch>
  </Container>
);

export default AdminHome;
