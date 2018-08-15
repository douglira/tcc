import styled from 'styled-components';
import { colors } from '~/styles';

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  height: 100%;
  width: 100%;
  background: ${props => props.theme.main.white};
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
  background: ${props => props.theme.main.lighter};
  margin-top: 50px;
  padding: 20px;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16), 0 3px 6px rgba(0, 0, 0, 0.23);

  h1 {
    font-size: 20px;
    font-weight: 400;
    color: ${props => props.theme.main.darker};
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
      color: ${props => props.theme.main.lighter};
      border: 0;
      background: ${props => props.theme.main.secondary};
      font-size: 14px;
      font-weight: 700;
      border-radius: 1px;
      cursor: pointer;

      &:hover {
        background: ${props => props.theme.main.secondaryLighter};
      }
    }
  }
`;

export const MaterialUI = () => ({
  cssLabel: {
    '&$cssFocused': {
      color: colors.main.primary,
    },
  },
  cssFocused: {},
  cssUnderline: {
    '&:after': {
      borderBottomColor: colors.main.primary,
    },
  },
  cssUnderlineError: {
    '&:after': {
      borderBottomColor: colors.main.danger,
    },
    '&:before': {
      borderBottomColor: colors.main.danger,
    },
  },
  formControl: {
    width: '100%',
    marginTop: '15px',
  },
  inputLabel: {
    fontSize: '15px',
  },
  inputText: {
    fontSize: '14px',
  },
});
