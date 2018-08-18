import { createActions, createReducer } from 'reduxsauce';

export const { Types, Creators } = createActions(
  {
    verifyAuth: [],

    signinRequest: ['credentials'],
    signinSuccess: ['user'],
    signinFailure: ['error'],

    signout: (logout = true) => ({ type: 'SIGNOUT', logout }),
  },
  { prefix: 'user/' },
);

const INITIAL_STATE = {
  isAuthenticated: false,
  isSigningin: false,
  loading: false,
  error: null,
  data: {
    role: '',
  },
};

const verifyAuth = (state = INITIAL_STATE) => ({ ...state, loading: true });

const signinRequest = (state = INITIAL_STATE) => ({
  ...state,
  isSigningin: true,
  loading: false,
  error: null,
  isAuthenticated: false,
});

const signinSuccess = (state = INITIAL_STATE, action) => ({
  ...state,
  isSigningin: false,
  loading: false,
  isAuthenticated: true,
  data: action.user,
});

const signinFailure = (state = INITIAL_STATE, action) => ({
  ...state,
  isSigningin: false,
  loading: false,
  isAuthenticated: false,
  error: action.error,
});

export default createReducer(INITIAL_STATE, {
  [Types.VERIFY_AUTH]: verifyAuth,

  [Types.SIGNIN_REQUEST]: signinRequest,
  [Types.SIGNIN_SUCCESS]: signinSuccess,
  [Types.SIGNIN_FAILURE]: signinFailure,
});
