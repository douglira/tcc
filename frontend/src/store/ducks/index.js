import { combineReducers } from 'redux';

import toastify from './toastify';

import user from './user';
import admin from './admin';

export default combineReducers({
  toastify,
  user,
  admin,
});
