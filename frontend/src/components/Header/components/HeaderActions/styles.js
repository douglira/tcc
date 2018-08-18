import styled from 'styled-components';

export const Container = styled.section`
  display: flex;
  flex: 1;
  align-self: stretch;
  flex-direction: row;
  justify-content: flex-end;
  align-items: center;

  & > * {
    margin-right: 0 !important;
    margin-left: 10px !important;
  }

  & > a {
    align-self: flex-end;
    font-size: 14px !important;
    font-weight: 500 !important;
    flex: 0 !important;
  }

  & > button {
    display: flex;
    justify-content: center;
    align-items: flex-end;
    border: 0;
    background: inherit;
    padding: 7px;
    box-sizing: border-box;
    border-radius: 50%;
    margin-right: 10px !important;
    margin-left: 0 !important;

    &:hover {
      background: ${props => props.theme.main.light.lighten};
    }

    img {
      align-self: flex-end;
      box-sizing: border-box;
      width: 24px;
      height: 24px;
      cursor: pointer;
    }
  }

  p {
    font-size: 14px;

    i {
      font-weight: bold;
      margin-left: 5px !important;
      padding: 7px;
      border-radius: 50%;
      cursor: pointer;

      &:hover {
        background: ${props => props.theme.main.light.lighten};
      }
    }
  }
`;
