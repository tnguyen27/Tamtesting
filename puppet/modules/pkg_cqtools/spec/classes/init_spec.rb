require 'spec_helper'
describe 'pkg_cqtools' do

  context 'with defaults for all parameters' do
    it { should contain_class('pkg_cqtools') }
  end
end
