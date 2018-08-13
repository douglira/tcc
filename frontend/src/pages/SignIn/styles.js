import styled from 'styled-components';

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  height: 100%;
  width: 100%;
  background: #f9f9f9;
  padding: 20px;
`;

export const Content = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  max-width: 400px;
  min-height: fit-content;
  background: #f1f1f1;
  margin-top: 50px;
  padding: 20px;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16), 0 3px 6px rgba(0, 0, 0, 0.23);

  h1 {
    font-size: 20px;
    font-weight: 400;
    color: #666;
    text-transform: uppercase;
    padding: 0 15px;
  }

  form {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
    max-width: 80%;

    button {
      margin-top: 30px;
      padding: 10px;
      align-self: stretch;
      color: #fff;
      border: 0;
      background: #ff5722;
      font-size: 14px;
      font-weight: 700;
      border-radius: 1px;
      cursor: pointer;

      &:hover {
        background: #cc4318;
      }
    }
  }
`;

export const MaterialUI = () => ({
  cssLabel: {
    '&$cssFocused': {
      color: '#ff5722',
    },
  },
  cssFocused: {},
  cssUnderline: {
    '&:after': {
      borderBottomColor: '#ff5722',
    },
  },
  formControl: {
    width: '100%',
    marginTop: '15px',
  },
  inputLabel: {
    fontSize: '14px',
  },
  inputText: {
    fontSize: '14px',
  },
});
