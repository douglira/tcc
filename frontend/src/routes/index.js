import React from 'react';
import { Switch, Route, Redirect } from 'react-router-dom';

import PrivateRoute from '~/routes/PrivateRoute';

import AdminHomePage from '~/pages/Admin/Home';

import LoginPage from '~/pages/SignIn';

const Routes = () => (
  <Switch>
    <Route exact path="/signin" component={LoginPage} />
    <PrivateRoute exact path="/admin/home" component={AdminHomePage} />
    <Route render={() => <Redirect to="/signin" />} />
  </Switch>
);

export default Routes;
