import { combineReducers } from 'redux';

import toastify from './toastify';

import user from './user';

export default combineReducers({
  toastify,
  user,
});
