import React from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { ThemeProvider } from 'styled-components';
import { colors } from '~/styles';

import { Container } from './styles';

import { connect } from 'react-redux';

import CartIcon from '~/assets/images/cart_icon.svg';
import WishlistIcon from '~/assets/images/wishlist_icon.svg';

const HeaderActions = ({ user }) => (
  <ThemeProvider theme={colors}>
    <Container>
      <button type="button">
        <img src={WishlistIcon} alt="Lista de desejos" />
      </button>
      <button type="button">
        <img src={CartIcon} alt="Carrinho" />
      </button>
      {user.isAuthenticated ? (
        <p>
          Olá, <strong>{user.displayName}</strong> <i className="fa fa-angle-down" />
        </p>
      ) : (
        <Link to="/signin">Minha conta</Link>
      )}
    </Container>
  </ThemeProvider>
);

HeaderActions.propTypes = {
  user: PropTypes.shape({
    isAuthenticated: PropTypes.bool,
    displayName: PropTypes.string,
  }).isRequired,
};

const mapStateToProps = state => ({
  user: {
    isAuthenticated: state.user.isAuthenticated,
    displayName: state.user.data.displayName,
  },
});

export default connect(mapStateToProps)(HeaderActions);
