import styled from 'styled-components';
import { colors } from '~/styles';

export const Container = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
`;

export const HeaderAdmin = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: flex-start !important;
  align-items: center;
  width: 100%;
  padding-bottom: 10px;
  margin-bottom: 15px;
  border-bottom: 0.625px solid;
  border-bottom-color: ${props => props.theme.main.dark.normal};

  & > p {
    font-size: 26px;
    margin-left: 15px;
    color: ${props => props.theme.main.dark.normal};
    font-weight: 700;
  }
`;

export const DrawerOptionsContainer = styled.div`
  width: 250px;

  dl {
    width: 100%;
    display: flex;
    flex-direction: row;

    dt {
      display: flex;
      justify-content: flex-start;
      align-items: center;
      line-height: 24px;
      padding: 10px 5px;

      a {
        text-decoration: inherit;
        font-size: 16px;
        margin-left: 5px;
        color: ${props => props.theme.main.dark.normal};
        font-weight: 500;

        &:hover {
          color: ${props => props.theme.main.primary.normal};
        }
      }
    }
  }
`;

export const MaterialUI = theme => ({
  button: {
    margin: theme.spacing.unit,
    width: '28px',
    height: '28px',
    color: colors.main.dark.normal,
  },
  input: {
    display: 'none',
  },
});
