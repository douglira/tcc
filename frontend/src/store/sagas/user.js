import { takeLatest, call, put } from 'redux-saga/effects';
import Cookies from 'js-cookie';
import api from '~/services/api';

import { Types as UserTypes, Creators as UserActions } from '~/store/ducks/user';
import { Types as ToastifyTypes, buildToastify } from '~/store/ducks/toastify';

function* verifyAuth() {
  try {
    const token = Cookies.get('SS_TOKEN');

    if (token) {
      const { data } = yield call(api.get, '/users/me');

      yield put(UserActions.signinSuccess(data.user));
    } else {
      yield put(UserActions.signout());
    }
  } catch (err) {
    yield put(UserActions.signout(false));
  }
}

function* signin(action) {
  try {
    const { data } = yield call(api.post, '/auth/signin', action.credentials);

    Cookies.set('SS_TOKEN', data.token);

    yield put(UserActions.signinSuccess(data.user));
  } catch (err) {
    if (err.response.data && err.response.data.error) {
      yield put({
        ...UserActions.signinFailure(err.response.data.error),
        toast: buildToastify(err.response.data.error, ToastifyTypes.ERROR),
      });
    }
  }
}

export default function* root() {
  yield takeLatest(UserTypes.VERIFY_AUTH, verifyAuth);
  yield takeLatest(UserTypes.SIGNIN_REQUEST, signin);

  yield put(UserActions.verifyAuth());
}
