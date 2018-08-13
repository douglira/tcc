import React, { Fragment } from 'react';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';

import { ToastContainer } from 'react-toastify';

import '~/config/reactotron';
import '~/styles/global';

import { Container, Content } from './styles/components';

import Header from '~/components/Header';

import store from '~/store';
import Routes from '~/routes';

const App = () => (
  <Provider store={store}>
    <BrowserRouter>
      <Fragment>
        <Header />
        <ToastContainer />
        <Container>
          <Content>
            <Routes />
          </Content>
        </Container>
      </Fragment>
    </BrowserRouter>
  </Provider>
);

export default App;
