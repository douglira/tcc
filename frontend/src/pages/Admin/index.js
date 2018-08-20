import React from 'react';
import PropTypes from 'prop-types';
import { Switch, Route, Redirect } from 'react-router-dom';

import { Container } from './styles';

import Home from './pages/Home';

const Admin = ({ match }) => (
  <Container>
    <Switch>
      <Route exact path={`${match.path}`} component={Home} />
      <Route render={() => <Redirect to={`${match.path}`} />} />
    </Switch>
  </Container>
);

Admin.propTypes = {
  match: PropTypes.shape({
    path: PropTypes.string,
  }).isRequired,
};

export default Admin;
