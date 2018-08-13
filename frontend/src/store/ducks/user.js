export const Types = {
  SIGNIN_REQUEST: 'user/SIGNIN_REQUEST',
  SIGNIN_SUCCESS: 'user/SIGNIN_SUCCESS',
  SIGNIN_FAILURE: 'user/SIGNIN_FAILURE',
};

export const Creators = {
  signinRequest: credentials => ({
    type: Types.SIGNIN_REQUEST,
    payload: { credentials },
  }),

  signinSuccess: user => ({
    type: Types.SIGNIN_SUCCESS,
    payload: { user },
  }),

  signinFailure: error => ({
    type: Types.SIGNIN_FAILURE,
    payload: { error },
  }),
};

const INITIAL_STATE = {
  isAuthenticated: false,
  // checking: false,
  loading: false,
  error: null,
  data: {},
};

export default function userReducer(state = INITIAL_STATE, action) {
  switch (action.type) {
    case Types.SIGNIN_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
        isAuthenticated: false,
      };
    case Types.SIGNIN_SUCCESS:
      return {
        ...state,
        loading: false,
        isAuthenticated: true,
        data: action.payload.user,
      };
    case Types.SIGNIN_FAILURE:
      return {
        ...state,
        loading: false,
        isAuthenticated: false,
        error: action.payload.error,
      };
    default:
      return state;
  }
}
