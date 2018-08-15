import React from 'react';
import { Switch, Route, Redirect } from 'react-router-dom';

import PrivateRoute from './PrivateRoute';
import AdminRoute from './AdminRoute';

import IndexPage from '~/pages/Index';
import LoginPage from '~/pages/SignIn';

import UserHomePage from '~/pages/User/Home';

import AdminHomePage from '~/pages/Admin';

const Routes = () => (
  <Switch>
    <Route exact path="/" component={IndexPage} />
    <Route exact path="/signin" component={LoginPage} />
    <PrivateRoute exact path="/home" component={UserHomePage} />
    <AdminRoute exact path="/admin/home" component={AdminHomePage} />
    <Route render={() => <Redirect to="/" />} />
  </Switch>
);

export default Routes;
