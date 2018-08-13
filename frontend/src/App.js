import React, { Fragment } from 'react';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';

import { ToastContainer } from 'react-toastify';

import '~/config/reactotron';
import '~/styles/global';

import Header from '~/components/Header';

import store from '~/store';
import Routes from '~/routes';

const App = () => (
  <Provider store={store}>
    <BrowserRouter>
      <Fragment>
        <Header />
        <ToastContainer />
        <Routes />
      </Fragment>
    </BrowserRouter>
  </Provider>
);

export default App;
