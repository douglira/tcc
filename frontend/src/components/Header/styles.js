import styled from 'styled-components';

export const HeaderContainer = styled.header`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  height: 120px;
  width: 100%;
  background: #eee;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16), 0 3px 6px rgba(0, 0, 0, 0.23);
  margin-bottom: 15px;
  padding: 10px;

  nav {
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    width: 100%;
    max-width: 920px;
    height: 50px;

    a {
      text-decoration: inherit;
      padding: 10px;
      border-radius: 2px;
      flex: 1;
      font-size: 16px;
      color: #ff5722;
      font-weight: 400;
      text-align: center;

      &:hover {
        background: rgba(190, 190, 190, 0.4);
      }
    }
  }
`;

export const SearchBar = styled.div`
  display: flex;
  justify-content: space-between;
  width: 100%;
  max-width: 920px;
  height: 50px;

  a {
    text-decoration: inherit;
    border-radius: 2px;
    font-size: 20px;
    color: #ff5722;
    font-weight: 400;
    text-align: center;
    font-weight: bold;
    display: flex;
    align-items: center;
    margin-right: 20px;
  }

  form {
    flex: 1;
    align-self: stretch;
    display: flex;
    flex-direction: row;
    background: #fff;
    max-width: 80%;

    input {
      flex: 1;
      align-self: stretch;
      border: 0;
      border-radius: 1.25px;
      padding: 10px;
      font-size: 14px;
      color: #333;
    }

    button {
      padding: 5px 15px;
      align-self: stretch;
      font-size: 14px;
      color: #ff5722;
      border: 0;
      background: inherit;
      cursor: pointer;

      &:hover {
        background: rgba(155, 155, 155, 0.3);
      }
    }
  }
`;
