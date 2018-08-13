import React from 'react';
import { NavLink } from 'react-router-dom';

import { HeaderContainer } from './styles';

const Header = () => (
  <HeaderContainer>
    <nav>
      <NavLink to="/">LOGOTIPO</NavLink>
      <NavLink to="/">Ofertas</NavLink>
      <NavLink to="/">Destaques</NavLink>
      <NavLink to="/">Pra você</NavLink>
    </nav>
  </HeaderContainer>
);

export default Header;
