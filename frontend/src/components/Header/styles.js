import styled from 'styled-components';

export const HeaderContainer = styled.header`
  display: flex;
  justify-content: center;
  height: 120px;
  width: 100%;
  background: #eee;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16), 0 3px 6px rgba(0, 0, 0, 0.23);
  margin-bottom: 15px;

  nav {
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    width: 100%;
    max-width: 80%;
    height: 100%;

    a {
      text-decoration: inherit;
      padding: 10px;
      border-radius: 2px;
      flex: 1;
      font-size: 16px;
      color: #ff5722;
      font-weight: 300;
      text-align: center;

      &:hover {
        background: rgba(190, 190, 190, 0.4);
      }
    }
  }
`;
