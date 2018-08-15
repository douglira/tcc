import React from 'react';
import { NavLink, Link } from 'react-router-dom';
import { ThemeProvider } from 'styled-components';
import { colors } from '~/styles';

import HeaderActions from './components/HeaderActions';
import { HeaderContainer, SearchBar, NavBar } from './styles';

const Header = () => (
  <ThemeProvider theme={colors}>
    <HeaderContainer>
      <SearchBar>
        <Link to="/">LOGOTIPO</Link>
        <form>
          <input type="search" placeholder="Busque o produto que deseja..." />
          <button type="submit">Procurar</button>
        </form>
        <HeaderActions />
      </SearchBar>
      <NavBar>
        <nav>
          <NavLink to="/">Categorias</NavLink>
          <NavLink to="/">Ofertas</NavLink>
          <NavLink to="/">Destaques</NavLink>
          <NavLink to="/">Pra você</NavLink>
        </nav>
      </NavBar>
    </HeaderContainer>
  </ThemeProvider>
);

export default Header;
