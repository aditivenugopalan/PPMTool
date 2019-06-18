import React, { Component } from "react";
import { Link } from "react-router-dom";
import Backlog from "./Backlog";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { getBacklog, sortBasedOn } from "../../actions/backlogActions";
import { Dropdown } from "react-bootstrap";

class ProjectBoard extends Component {
  //constructor to handle errors
  constructor() {
    super();
    this.state = {
      errors: {}
    };
    this.onSort = this.onSort.bind(this);
  }

  async onSort(e, sortBy) {
    const { id } = this.props.match.params;
    this.props.sortBasedOn(id, sortBy);
  }

  componentDidMount() {
    const { id } = this.props.match.params;
    this.props.getBacklog(id);
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.errors) {
      this.setState({ errors: nextProps.errors });
    }
  }

  render() {
    const { id } = this.props.match.params;
    const { project_tasks } = this.props.backlog;
    const { errors } = this.state;

    let BoardContent;

    const boardAlgorithm = (errors, project_tasks) => {
      if (project_tasks.length < 1) {
        if (errors.projectNotFound) {
          return (
            <div className="aleart alert-danger text-center" role="alert">
              {errors.projectNotFound}
            </div>
          );
        } else if (errors.projectIdentifier) {
          return (
            <div className="aleart alert-danger text-center" role="alert">
              {errors.projectIdentifier}
            </div>
          );
        } else {
          return (
            <div className="alert alert-info text-center" role="alert">
              No project Tasks on this board
            </div>
          );
        }
      } else {
        return <Backlog project_tasks_prop={project_tasks} />;
      }
    };

    BoardContent = boardAlgorithm(errors, project_tasks);

    return (
      <div className="container">
        <Link to={`/addProjectTask/${id}`} className="btn btn-primary mb-3">
          <i className="fas fa-plus-circle"> Create Project Task</i>
        </Link>
        <br />

        <Dropdown>
          <Dropdown.Toggle variant="primary" id="dropdown-basic">
            Sort By
          </Dropdown.Toggle>

          <Dropdown.Menu>
            <Dropdown.Item onClick={e => this.onSort(e, "priority")}>
              Priority
            </Dropdown.Item>
            <Dropdown.Item onClick={e => this.onSort(e, "duedate")}>
              Due Date
            </Dropdown.Item>
            <Dropdown.Item onClick={e => this.onSort(e, "sequence")}>
              Project Sequence
            </Dropdown.Item>
          </Dropdown.Menu>
        </Dropdown>
        <br />
        <hr />
        {BoardContent}
      </div>
    );
  }
}

ProjectBoard.propTypes = {
  backlog: PropTypes.object.isRequired,
  getBacklog: PropTypes.func.isRequired,
  sortBasedOn: PropTypes.func.isRequired,
  errors: PropTypes.object.isRequired
};

const mapStatetoProps = state => ({
  backlog: state.backlog,
  errors: state.errors
});

export default connect(
  mapStatetoProps,
  { getBacklog, sortBasedOn }
)(ProjectBoard);
