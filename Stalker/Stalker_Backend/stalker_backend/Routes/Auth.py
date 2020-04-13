from flask import Blueprint, jsonify, request
from flask_restful import abort
from flask_jwt_extended import create_access_token, jwt_required, get_jwt_identity

from ..Models.Admin import Admin
from ..Models.Organization import Organization
from ..Utils.AuthUtils import check_admin_permission

auth_routes = Blueprint('auth', __name__, url_prefix='/')


@auth_routes.route('login', methods=['POST'])
def login():
    email = request.form['email']
    # TODO: password = request.form['password']
    admin = Admin.query.filter(Admin.email == email).first_or_404(description='username or password incorrect')
    # TODO check password
    admin_info = admin.to_dict(None)
    access_token = create_access_token(admin_info)
    return jsonify(access_token=access_token), 200


@auth_routes.route('/select-organization', methods=['GET'])
@jwt_required
def select_organization():
    organization_id = request.args.get('organization_id')
    if organization_id:
        admin = get_jwt_identity()
        if check_admin_permission(admin['id'], Organization.query.get(organization_id)):
            new_admin_info = Admin.query.get(admin['id']).to_dict(organization_id)
            return jsonify(access_token=create_access_token(new_admin_info)), 200
        else:
            abort(403)
    else:
        abort(400)
