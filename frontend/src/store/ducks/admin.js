export const Types = {
  ALL_USERS_REQUEST: 'admin/ALL_USERS_REQUEST',
  ALL_USERS_SUCCESS: 'admin/ALL_USERS_SUCCESS',
  ALL_USERS_FAILURE: 'admin/ALL_USERS_FAILURE',
};

export const Creators = {
  allUsersRequest: (page, perPage) => ({
    type: Types.ALL_USERS_REQUEST,
    payload: { page, perPage },
  }),
  allUsersSuccess: data => ({
    type: Types.ALL_USERS_SUCCESS,
    payload: { data },
  }),
  allUsersFailure: error => ({
    type: Types.ALL_USERS_FAILURE,
    payload: { error },
  }),
};

const INITIAL_STATE = {
  allUsers: {
    loading: false,
    error: null,
    total: 0,
    page: 1,
    perPage: 20,
    lastPage: 0,
    data: [],
  },
};

export default function adminReducer(state = INITIAL_STATE, action) {
  switch (action.type) {
    case Types.ALL_USERS_REQUEST:
      return { ...state, allUsers: { ...state.allUsers, loading: true, error: null } };
    case Types.ALL_USERS_SUCCESS:
      return { ...state, allUsers: { ...state.allUsers, loading: false, ...action.payload.data } };
    case Types.ALL_USERS_FAILURE:
      return {
        ...state,
        allUsers: { ...state.allUsers, loading: false, error: action.payload.error },
      };
    default:
      return state;
  }
}
