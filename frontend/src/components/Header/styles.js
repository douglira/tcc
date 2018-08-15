import styled from 'styled-components';

export const HeaderContainer = styled.header`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  height: 150px;
  width: 100%;
  background: ${props => props.theme.main.lighter};
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16), 0 3px 6px rgba(0, 0, 0, 0.23);
  margin-bottom: 15px;
  padding: 10px 0 0 0;
`;

export const SearchBar = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  width: 100%;
  max-width: 920px;
  height: 50px;
  margin-top: 15px;

  a {
    text-decoration: inherit;
    font-size: 20px;
    color: ${props => props.theme.main.darker};
    font-weight: 400;
    text-align: center;
    font-weight: bold;
    display: flex;
    align-items: center;
    margin-right: 50px;
  }

  form {
    flex: 1;
    align-self: center;
    height: 38px;
    display: flex;
    flex-direction: row;
    background: inherit;
    max-width: 50%;
    border-radius: 2px;

    input {
      flex: 1;
      align-self: stretch;
      border: 0;
      border-radius: 1.25px;
      padding: 10px;
      box-sizing: border-box;
      font-size: 14px;
      color: ${props => props.theme.main.regular};
      border-radius: 2px 0 0 2px;
      background: ${props => props.theme.main.white};

      &::placeholder {
        color: ${props => props.theme.main.regular};
      }
    }

    button {
      padding: 5px 15px;
      align-self: stretch;
      font-size: 14px;
      background: ${props => props.theme.main.primary};
      border: 0;
      color: ${props => props.theme.main.white};
      cursor: pointer;
      border-radius: 0 2px 2px 0;
      font-weight: 700;

      &:hover {
        background: ${props => props.theme.main.primaryLighter};
      }
    }
  }
`;

export const NavBar = styled.section`
  display: flex;
  width: 100%;
  justify-content: center;
  background: ${props => props.theme.main.primary};

  nav {
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    width: 100%;
    max-width: 920px;
    height: 35px;

    a {
      display: flex;
      justify-content: center;
      align-items: center;
      align-self: stretch;
      text-decoration: inherit;
      border-radius: 2px;
      flex: 1;
      font-size: 16px;
      color: ${props => props.theme.main.darker};
      font-weight: 600;
      text-align: center;

      &:hover {
        background: ${props => props.theme.main.primaryLighter};
      }

      &:first-child {
        display: flex;
        justify-content: space-evenly;
        align-items: center;
      }
    }
  }
`;
