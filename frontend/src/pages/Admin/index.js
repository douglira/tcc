import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  Switch, Route, Redirect, Link,
} from 'react-router-dom';

import { withStyles } from '@material-ui/core/styles';
import { Drawer, IconButton } from '@material-ui/core';
import { MenuRounded, PeopleRounded } from '@material-ui/icons';

import {
  Container, HeaderAdmin, DrawerOptionsContainer, MaterialUI,
} from './styles';

import Home from './pages/Home';

class Admin extends Component {
  static propTypes = {
    classes: PropTypes.shape().isRequired,
    match: PropTypes.shape({
      path: PropTypes.string,
    }).isRequired,
  };

  state = {
    toggleDrawer: false,
    drawerOptions: [
      {
        label: 'Relatório de usuários',
        path: '/admin/home',
        key: 'userReport',
      },
    ],
  };

  toggleDrawer = open => () => {
    this.setState({
      toggleDrawer: open,
    });
  };

  renderDrawerOptions = () => (
    <DrawerOptionsContainer>
      <dl>
        {this.state.drawerOptions.map(option => (
          <dt key={option.key}>
            <PeopleRounded />
            <Link to={option.path}>{option.label}</Link>
          </dt>
        ))}
      </dl>
    </DrawerOptionsContainer>
  );

  render() {
    const { match, classes } = this.props;
    const { toggleDrawer } = this.state;

    return (
      <Container>
        <HeaderAdmin>
          <IconButton onClick={this.toggleDrawer(true)} className={classes.button}>
            <MenuRounded />
          </IconButton>
          <p>Área de Administrador</p>
        </HeaderAdmin>
        <Drawer anchor="left" open={toggleDrawer} onClose={this.toggleDrawer(false)}>
          {this.renderDrawerOptions()}
        </Drawer>
        <Switch>
          <Route exact path={`${match.path}`} component={Home} />
          <Route render={() => <Redirect to={`${match.path}`} />} />
        </Switch>
      </Container>
    );
  }
}

export default withStyles(MaterialUI)(Admin);
