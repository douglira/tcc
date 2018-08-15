import React from 'react';
import { Link } from 'react-router-dom';
import { ThemeProvider } from 'styled-components';
import { colors } from '~/styles';

import { Container } from './styles';

import CartIcon from '~/assets/images/cart_icon.svg';
import WishlistIcon from '~/assets/images/wishlist_icon.svg';

const HeaderActions = () => (
  <ThemeProvider theme={colors}>
    <Container>
      <button type="button">
        <img src={WishlistIcon} alt="Lista de desejos" />
      </button>
      <button type="button">
        <img src={CartIcon} alt="Carrinho" />
      </button>
      <Link to="/">Meus pedidos</Link>
      <Link to="/signin">Minha conta</Link>
    </Container>
  </ThemeProvider>
);

export default HeaderActions;
