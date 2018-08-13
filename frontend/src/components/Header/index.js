import React from 'react';
import { NavLink, Link } from 'react-router-dom';

import { HeaderContainer, SearchBar } from './styles';

const Header = () => (
  <HeaderContainer>
    <SearchBar>
      <Link to="/">LOGOTIPO</Link>
      <form>
        <input type="search" placeholder="Busque aqui o produto que deseja..." />
        <button type="submit">Procurar</button>
      </form>
    </SearchBar>
    <nav>
      <NavLink to="/">Categorias</NavLink>
      <NavLink to="/">Ofertas</NavLink>
      <NavLink to="/">Destaques</NavLink>
      <NavLink to="/">Pra você</NavLink>
    </nav>
  </HeaderContainer>
);

export default Header;
