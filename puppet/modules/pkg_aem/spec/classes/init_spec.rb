require 'spec_helper'
describe 'pkg_aem' do

  context 'with defaults for all parameters' do
    it { should contain_class('pkg_aem') }
  end
end
