import { all } from 'redux-saga/effects';

import userSaga from './user';

export default function* rootSagas() {
  yield all([userSaga()]);
}
